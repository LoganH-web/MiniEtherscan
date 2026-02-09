package com.example.minieetherscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "blocks", uniqueConstraints = {
        @UniqueConstraint(columnNames = "block_number", name = "uk_block_number")
})
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "block_number", nullable = false, unique = true)
    private Long blockNumber;

    @Column(name = "block_hash", nullable = false)
    private String blockHash;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    public Block() {
    }

    public Block(Long blockNumber, String blockHash, Long timestamp) {
        this.blockNumber = blockNumber;
        this.blockHash = blockHash;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", blockNumber=" + blockNumber +
                ", blockHash='" + blockHash + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
