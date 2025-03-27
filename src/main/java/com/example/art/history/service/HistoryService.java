package com.example.art.history.service;

import com.example.art.history.model.History;
import com.example.art.history.repository.HistoryRepository;
import com.example.art.order.model.Orders;
import com.example.art.user.model.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void deleteById(UUID id) {
        historyRepository.deleteById(id);
    }

    @Scheduled(fixedDelay = 31622400000L)
    public void deleteAllHistory() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);

        historyRepository.deleteByAddedOnBetween(startDate, endDate);
    }

    public void saveInHistory(Orders orders,
                              User user) {


        History history = History.builder()
                .addedOn(LocalDate.now())
                .user(user)
                .build();

        historyRepository.save(history);
    }
}
