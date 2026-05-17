package com.hotbyte.repository;

import com.hotbyte.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    // Get all transactions for user
    List<WalletTransaction> findByUserIdOrderByCreatedAtDesc(
            Long userId);
}