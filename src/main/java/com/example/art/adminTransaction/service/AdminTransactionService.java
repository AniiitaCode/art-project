package com.example.art.adminTransaction.service;

import com.example.art.adminTransaction.model.AdminTransaction;
import com.example.art.adminTransaction.model.TypeTransaction;
import com.example.art.adminTransaction.repository.AdminTransactionRepository;
import com.example.art.user.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminTransactionService {

    private final AdminTransactionRepository adminTransactionRepository;

    public AdminTransactionService(AdminTransactionRepository adminTransactionRepository) {
        this.adminTransactionRepository = adminTransactionRepository;
    }

    public void createAddTransaction(TypeTransaction typeTransaction, BigDecimal totalPrice, String username) {
        AdminTransaction transaction = AdminTransaction.builder()
                .transactionDate(LocalDateTime.now())
                .amount(totalPrice)
                .typeTransaction(typeTransaction)
                .username(username)
                .build();

        adminTransactionRepository.save(transaction);
    }

    public List<AdminTransaction> getAllTransaction() {

       return adminTransactionRepository.findAll(Sort.by(Sort.Order.desc("transactionDate")));
    }

    public void clearHistory(List<AdminTransaction> transactions) {
        adminTransactionRepository.deleteAll(transactions);
    }

    public void createWithdrawTransaction(User user, TypeTransaction typeTransaction, BigDecimal amount) {
        AdminTransaction transaction = AdminTransaction.builder()
                .transactionDate(LocalDateTime.now())
                .amount(amount)
                .typeTransaction(typeTransaction)
                .username(user.getUsername())
                .build();

        adminTransactionRepository.save(transaction);
    }
}
