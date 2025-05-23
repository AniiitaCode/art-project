package com.example.art.transaction.service;

import com.example.art.transaction.model.Transaction;
import com.example.art.transaction.model.TransactionType;
import com.example.art.transaction.repository.TransactionRepository;
import com.example.art.user.model.User;
import com.example.art.wallet.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(User user, Wallet wallet, BigDecimal amount, TransactionType transactionType) {

        Transaction transaction = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .amount(amount)
                .transactionType(transactionType)
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    public List<Transaction> getByUserId(UUID userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
    }

    @Transactional
    public void clearHistory(User user) {
        transactionRepository.deleteTransactionsByUser(user);
    }

    public void createWithdrawTransaction(User user, Wallet wallet, TransactionType transactionType, BigDecimal totalPrice) {

        Transaction transaction = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .amount(totalPrice)
                .transactionType(transactionType)
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }
}
