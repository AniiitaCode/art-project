package com.example.art.wallet.repository;

import com.example.art.user.model.User;
import com.example.art.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Wallet findByOwner(User user);
}
