package com.example.art.adminTransaction.repository;

import com.example.art.adminTransaction.model.AdminTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminTransactionRepository extends JpaRepository<AdminTransaction, UUID> {

    List<AdminTransaction> findByUsername(String username);
}
