package com.example.art.adminBalance.service;

import com.example.art.adminBalance.model.Balance;
import com.example.art.adminBalance.repository.AdminBalanceRepository;
import com.example.art.adminTransaction.model.TypeTransaction;
import com.example.art.adminTransaction.service.AdminTransactionService;
import com.example.art.exception.DomainException;
import com.example.art.user.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminBalanceService {

    private final AdminBalanceRepository adminBalanceRepository;
    private final AdminTransactionService adminTransactionService;

    public AdminBalanceService(AdminBalanceRepository adminBalanceRepository,
                               AdminTransactionService adminTransactionService) {
        this.adminBalanceRepository = adminBalanceRepository;
        this.adminTransactionService = adminTransactionService;
    }

    public BigDecimal getBalance() {
        Balance adminBalance = adminBalanceRepository.findFirst();

        if (adminBalance == null) {
            adminBalance = new Balance();
            adminBalanceRepository.save(adminBalance);
        }

        return adminBalance.getBalance();
    }

    public void addBalance(BigDecimal amount) {
        Balance adminBalance = adminBalanceRepository.findFirst();

        if (adminBalance == null) {
            adminBalance = new Balance();
            adminBalanceRepository.save(adminBalance);
        }

        adminBalance.setBalance(adminBalance.getBalance().add(amount));
        adminBalanceRepository.save(adminBalance);
    }

    public void withdrawBalance(User user, BigDecimal amount) {
        int result = getBalance().compareTo(amount);

        if (result < 0) {
            throw new DomainException("Your balance is insufficient.");
        }

        Balance adminBalance = adminBalanceRepository.findFirst();

        adminBalance.setBalance(adminBalance.getBalance().subtract(amount));
        adminTransactionService.createWithdrawTransaction(user, TypeTransaction.WITHDRAW, amount);

        adminBalanceRepository.save(adminBalance);
    }
}
