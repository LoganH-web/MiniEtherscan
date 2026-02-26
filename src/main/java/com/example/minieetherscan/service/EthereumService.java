package com.example.minieetherscan.service;

import com.example.minieetherscan.entity.Block;
import com.example.minieetherscan.entity.Transaction;
import com.example.minieetherscan.repository.BlockRepository;
import com.example.minieetherscan.repository.TransactionRepository;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;

import jakarta.annotation.PreDestroy;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EthereumService {

    private static final Logger log = LoggerFactory.getLogger(EthereumService.class);
    private static final int MAX_BLOCK_RETRIES = 3;
    private static final long BLOCK_RETRY_DELAY_MS = 2000;
    private static final long INITIAL_RECONNECT_DELAY_MS = 1000;
    private static final long MAX_RECONNECT_DELAY_MS = 30000;

    private final Web3j web3j;
    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;
    private Disposable blockSubscription;

    public EthereumService(Web3j web3j, BlockRepository blockRepository, TransactionRepository transactionRepository) {
        this.web3j = web3j;
        this.blockRepository = blockRepository;
        this.transactionRepository = transactionRepository;
    }

    public void logBlockchainInfo() {
        try {
            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();

            log.info("Connected to Sepolia. Latest block: {}", latestBlockNumber);
            log.info("Current gas price: {} Wei", gasPrice);
        } catch (Exception e) {
            log.error("Failed to fetch blockchain info", e);
        }
    }

    public void subscribeToNewBlocks() {
        log.info("Subscribing to new blocks...");

        blockSubscription = web3j.blockFlowable(true)
                .retryWhen(errors -> errors.zipWith(
                        Flowable.range(1, Integer.MAX_VALUE),
                        (error, attempt) -> attempt
                ).flatMap(attempt -> {
                    long delay = Math.min(
                            INITIAL_RECONNECT_DELAY_MS * (1L << (attempt - 1)),
                            MAX_RECONNECT_DELAY_MS
                    );
                    log.warn("Block subscription error (attempt {}), reconnecting in {}ms",
                            attempt, delay);
                    return Flowable.timer(delay, TimeUnit.MILLISECONDS);
                }))
                .subscribe(
                        ethBlock -> processBlock(ethBlock.getBlock()),
                        error -> log.error("Block subscription terminated unexpectedly", error)
                );
    }

    private void processBlock(EthBlock.Block block) {
        LocalDateTime timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(block.getTimestamp().longValue()),
                ZoneId.systemDefault()
        );

        log.info("New Block | Number: {} | Timestamp: {} | Tx Count: {}",
                block.getNumber(),
                timestamp,
                block.getTransactions().size());

        for (int attempt = 1; attempt <= MAX_BLOCK_RETRIES; attempt++) {
            try {
                saveBlock(block.getNumber().longValue(), block.getHash(), block.getTimestamp().longValue());
                indexTransactions(block);
                return;
            } catch (Exception e) {
                if (attempt == MAX_BLOCK_RETRIES) {
                    log.error("Failed to process block {} after {} attempts",
                            block.getNumber(), MAX_BLOCK_RETRIES, e);
                } else {
                    log.warn("Error processing block {} (attempt {}/{}), retrying in {}ms",
                            block.getNumber(), attempt, MAX_BLOCK_RETRIES, BLOCK_RETRY_DELAY_MS);
                    try {
                        Thread.sleep(BLOCK_RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    public void saveBlock(Long blockNumber, String blockHash, Long timestamp) {
        // Check if block already exists (idempotent)
        if (blockRepository.findByBlockNumber(blockNumber).isPresent()) {
            log.debug("Block {} already exists in database. Skipping.", blockNumber);
            return;
        }

        try {
            Block block = new Block(blockNumber, blockHash, timestamp);
            blockRepository.save(block);
            log.info("Block {} saved to database", blockNumber);
        } catch (DataIntegrityViolationException e) {
            // Handle race condition where block was inserted by another thread
            log.debug("Block {} already persisted by another process", blockNumber);
        }
    }

    private void indexTransactions(EthBlock.Block block) {
        Long blockNumber = block.getNumber().longValue();
        List<EthBlock.TransactionResult> transactions = block.getTransactions();

        int saved = 0;
        for (EthBlock.TransactionResult txResult : transactions) {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) txResult.get();
            if (saveTransaction(tx, blockNumber)) {
                saved++;
            }
        }

        if (!transactions.isEmpty()) {
            log.info("Block {} | Indexed {}/{} transactions", blockNumber, saved, transactions.size());
        }
    }

    private boolean saveTransaction(EthBlock.TransactionObject tx, Long blockNumber) {
        if (transactionRepository.findByTxHash(tx.getHash()).isPresent()) {
            log.debug("Transaction {} already exists. Skipping.", tx.getHash());
            return false;
        }

        try {
            Transaction transaction = new Transaction(
                    tx.getHash(),
                    tx.getFrom(),
                    tx.getTo(),
                    tx.getValue(),
                    null,  // gasUsed — populated in Phase 4 (receipt fetching)
                    null,  // status — populated in Phase 4 (receipt fetching)
                    blockNumber
            );
            transactionRepository.save(transaction);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.debug("Transaction {} already persisted by another process", tx.getHash());
            return false;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (blockSubscription != null && !blockSubscription.isDisposed()) {
            log.info("Disposing block subscription...");
            blockSubscription.dispose();
        }
    }
}
