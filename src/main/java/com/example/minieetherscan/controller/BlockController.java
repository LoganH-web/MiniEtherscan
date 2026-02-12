package com.example.minieetherscan.controller;

import com.example.minieetherscan.repository.BlockRepository;
import com.example.minieetherscan.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/blocks")
public class BlockController {

    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;

    public BlockController(BlockRepository blockRepository, TransactionRepository transactionRepository) {
        this.blockRepository = blockRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestBlock() {
        return blockRepository.findTopByOrderByBlockNumberDesc()
                .map(block -> {
                    long txCount = transactionRepository.countByBlockNumber(block.getBlockNumber());
                    return ResponseEntity.ok(Map.of(
                            "blockNumber", block.getBlockNumber(),
                            "blockHash", block.getBlockHash(),
                            "timestamp", block.getTimestamp(),
                            "txCount", txCount
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
