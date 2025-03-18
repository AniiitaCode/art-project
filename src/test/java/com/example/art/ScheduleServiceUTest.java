package com.example.art;

import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import com.example.art.schedule.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ScheduleServiceUTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void whenDeleteOrderById_thenDeleteOrder() {
        UUID orderId = UUID.randomUUID();

        scheduleService.deleteById(orderId);

        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void whenAcceptOrder_thenAcceptInSchedule() {
        Orders order = new Orders();

        scheduleService.acceptOrder(order);

        verify(orderRepository, times(1)).save(order);
    }
}
