package com.example.art.design.repository;

import com.example.art.design.model.Design;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DesignRepository extends JpaRepository<Design, UUID> {


    Design findByUserId(UUID userId);

    Design findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}
