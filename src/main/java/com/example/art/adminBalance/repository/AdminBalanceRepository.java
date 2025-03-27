package com.example.art.adminBalance.repository;

import com.example.art.adminBalance.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminBalanceRepository extends JpaRepository<Balance, UUID> {

    @Query("SELECT a FROM Balance a")
    Balance findFirst();
}
