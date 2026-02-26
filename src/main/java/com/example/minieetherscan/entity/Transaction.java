package com.example.minieetherscan.entity;

import jakarta.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "transactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = "tx_hash", name = "uk_tx_hash")
}, indexes = {
        @Index(columnList = "from_address", name = "idx_from_address"),
        @Index(columnList = "to_address", name = "idx_to_address"),
        @Index(columnList = "block_number", name = "idx_block_number")
})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_hash", nullable = false, unique = true)
    private String txHash;

    @Column(name = "from_address", nullable = false)
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "value", nullable = false)
    private BigInteger value;

    @Column(name = "gas_used")
    private BigInteger gasUsed;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    public Transaction() {
    }

    public Transaction(String txHash, String fromAddress, String toAddress, BigInteger value, BigInteger gasUsed, Boolean status, Long blockNumber) {
        this.txHash = txHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.value = value;
        this.gasUsed = gasUsed;
        this.status = status;
        this.blockNumber = blockNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public BigInteger getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", txHash='" + txHash + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", value=" + value +
                ", gasUsed=" + gasUsed +
                ", status=" + status +
                ", blockNumber=" + blockNumber +
                '}';
    }
}
