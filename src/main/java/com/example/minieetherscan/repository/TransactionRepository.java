package com.example.minieetherscan.repository;

import com.example.minieetherscan.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTxHash(String txHash);

    Page<Transaction> findByFromAddressOrToAddress(String fromAddress, String toAddress, Pageable pageable);
}
