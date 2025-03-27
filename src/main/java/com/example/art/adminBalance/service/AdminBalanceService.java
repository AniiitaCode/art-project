package com.example.art.adminBalance.service;

import com.example.art.adminBalance.model.Balance;
import com.example.art.adminBalance.repository.AdminBalanceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminBalanceService {

    private final AdminBalanceRepository adminBalanceRepository;

    public AdminBalanceService(AdminBalanceRepository adminBalanceRepository) {
        this.adminBalanceRepository = adminBalanceRepository;
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
}
