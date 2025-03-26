package com.example.art.wallet.service;

import com.example.art.exception.DomainException;
import com.example.art.transaction.model.TransactionType;
import com.example.art.transaction.service.TransactionService;
import com.example.art.user.model.User;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public WalletService(WalletRepository walletRepository,
                         TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public void createWallet(User user) {

        Wallet wallet = Wallet.builder()
                .balance(new BigDecimal("20.00"))
                .currency("BGN")
                .owner(user)
                .createdOn(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);
    }

    public Wallet getWalletByUser(User user) {
        return walletRepository.findByOwner(user);
    }

    public Wallet addBalance(User user, BigDecimal newSum) {
        Wallet wallet = getWalletByUser(user);
        wallet.setBalance(wallet.getBalance().add(newSum));

        walletRepository.save(wallet);
        return wallet;
    }

    public void withdrawBalance(User user, BigDecimal totalPrice) {
        Wallet wallet = getWalletByUser(user);
        int result = wallet.getBalance().compareTo(totalPrice);

        if (result < 0) {
            throw new DomainException("Your balance is insufficient.");
        }

        transactionService.createWithdrawTransaction(user, wallet, TransactionType.WITHDRAW, totalPrice);
        wallet.setBalance(wallet.getBalance().subtract(totalPrice));

        walletRepository.save(wallet);
    }
}
