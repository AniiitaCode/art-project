package com.example.art.history.service;

import com.example.art.design.model.Design;
import com.example.art.history.model.History;
import com.example.art.history.repository.HistoryRepository;
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
        historyRepository.deleteAll();
    }

    public void saveInHistory(Design design) {
        History history = History.builder()
                .user(design.getUser())
                .addedOn(LocalDate.now())
                .build();

        historyRepository.save(history);
    }
}
