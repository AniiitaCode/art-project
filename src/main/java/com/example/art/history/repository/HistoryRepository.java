package com.example.art.history.repository;

import com.example.art.history.model.History;
import com.example.art.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID> {

    void deleteByAddedOnBetween(LocalDate startDate, LocalDate endDate);

    List<History> findByUser(User user);
}
