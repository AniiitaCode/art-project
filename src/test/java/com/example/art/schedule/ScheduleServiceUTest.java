package com.example.art.schedule;

import com.example.art.history.service.HistoryService;
import com.example.art.order.model.OrderStatus;
import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import com.example.art.order.service.OrderService;
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
    private OrderService orderService;

    @Mock
    private HistoryService historyService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void whenDeleteOrderById_thenDeleteOrder() {
        UUID orderId = UUID.randomUUID();
        Orders order = mock(Orders.class);

        when(orderService.getOrderById(orderId)).thenReturn(order);

        scheduleService.deleteById(orderId);

        verify(order).setStatus(OrderStatus.UNACCEPTED);
        verify(orderRepository).save(order);
        verify(historyService).updateStatusHistory(order);
    }

    @Test
    void whenAcceptOrder_thenAcceptInSchedule() {
        Orders order = mock(Orders.class);

        scheduleService.acceptOrder(order);

        verify(order).setStatus(OrderStatus.ACCEPTED);
        verify(orderRepository).save(order);
        verify(historyService).updateStatusHistory(order);
    }
}
