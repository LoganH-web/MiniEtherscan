package com.example.minieetherscan.service;

import com.example.minieetherscan.entity.Block;
import com.example.minieetherscan.entity.Transaction;
import com.example.minieetherscan.repository.BlockRepository;
import com.example.minieetherscan.repository.TransactionRepository;
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

@Service
public class EthereumService {

    private static final Logger log = LoggerFactory.getLogger(EthereumService.class);
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

        blockSubscription = web3j.blockFlowable(true).subscribe(
                ethBlock -> {
                    EthBlock.Block block = ethBlock.getBlock();
                    LocalDateTime timestamp = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(block.getTimestamp().longValue()),
                            ZoneId.systemDefault()
                    );

                    log.info("New Block | Number: {} | Timestamp: {} | Tx Count: {}",
                            block.getNumber(),
                            timestamp,
                            block.getTransactions().size());

                    // Save block to database (idempotent)
                    saveBlock(block.getNumber().longValue(), block.getHash(), block.getTimestamp().longValue());

                    // Index transactions from this block
                    indexTransactions(block);
                },
                error -> log.error("Error in block subscription", error)
        );
    }

    public void saveBlock(Long blockNumber, String blockHash, Long timestamp) {
        try {
            // Check if block already exists (idempotent)
            if (blockRepository.findByBlockNumber(blockNumber).isPresent()) {
                log.debug("Block {} already exists in database. Skipping.", blockNumber);
                return;
            }

            Block block = new Block(blockNumber, blockHash, timestamp);
            blockRepository.save(block);
            log.info("Block {} saved to database", blockNumber);
        } catch (DataIntegrityViolationException e) {
            // Handle race condition where block was inserted by another thread
            log.debug("Block {} already persisted by another process", blockNumber);
        } catch (Exception e) {
            log.error("Error saving block {}", blockNumber, e);
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
        try {
            if (transactionRepository.findByTxHash(tx.getHash()).isPresent()) {
                log.debug("Transaction {} already exists. Skipping.", tx.getHash());
                return false;
            }

            Transaction transaction = new Transaction(
                    tx.getHash(),
                    tx.getFrom(),
                    tx.getTo(),
                    tx.getValue().toString(),
                    tx.getGas().longValue(),
                    blockNumber
            );
            transactionRepository.save(transaction);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.debug("Transaction {} already persisted by another process", tx.getHash());
            return false;
        } catch (Exception e) {
            log.error("Error saving transaction {}", tx.getHash(), e);
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
