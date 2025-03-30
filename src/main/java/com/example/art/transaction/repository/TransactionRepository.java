package com.example.art.transaction.repository;

import com.example.art.transaction.model.Transaction;
import com.example.art.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    void deleteTransactionsByUser(User user);

    List<Transaction> findByUserIdOrderByTransactionDateDesc(UUID userId);
}
