package com.example.art.history;

import com.example.art.history.model.History;
import com.example.art.history.repository.HistoryRepository;
import com.example.art.history.service.HistoryService;
import com.example.art.order.model.Orders;
import com.example.art.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableScheduling
public class HistoryServiceUTest {

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private HistoryService historyService;

    @Test
    void whenDeleteId_thenDeleteHistory() {
        UUID historyId = UUID.randomUUID();

        historyService.deleteById(historyId);

        verify(historyRepository, times(1)).deleteById(historyId);
    }

    @Test
    void whenSaveHistory_thenSuccessSaveHistoryInDatabase() {
        Orders orders = mock(Orders.class);
        User user = new User();

        historyService.saveInHistory(orders, user);

        ArgumentCaptor<History> historyArgumentCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository, times(1)).save(historyArgumentCaptor.capture());
    }

    @Test
    void whenDeleteAllHistory_thenEmptyHistoryRepository() {
        historyService.deleteAllHistory();

        ArgumentCaptor<LocalDate> startDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endDateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        verify(historyRepository, times(1)).deleteByAddedOnBetween(startDateCaptor.capture(), endDateCaptor.capture());
    }
}
