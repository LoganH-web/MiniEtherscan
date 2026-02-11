package com.example.minieetherscan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = "tx_hash", name = "uk_tx_hash")
}, indexes = {
        @Index(columnList = "from_address", name = "idx_from_address"),
        @Index(columnList = "to_address", name = "idx_to_address")
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
    private String value;

    @Column(name = "gas", nullable = false)
    private Long gas;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    public Transaction() {
    }

    public Transaction(String txHash, String fromAddress, String toAddress, String value, Long gas, Long blockNumber) {
        this.txHash = txHash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.value = value;
        this.gas = gas;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getGas() {
        return gas;
    }

    public void setGas(Long gas) {
        this.gas = gas;
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
                ", value='" + value + '\'' +
                ", gas=" + gas +
                ", blockNumber=" + blockNumber +
                '}';
    }
}
