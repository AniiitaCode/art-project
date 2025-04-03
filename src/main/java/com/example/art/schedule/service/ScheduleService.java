package com.example.art.schedule.service;

import com.example.art.history.service.HistoryService;
import com.example.art.order.model.OrderStatus;
import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import com.example.art.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ScheduleService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;;
    private final HistoryService historyService;

    public ScheduleService(OrderRepository orderRepository,
                           OrderService orderService,
                           HistoryService historyService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.historyService = historyService;
    }

    public void acceptOrder(Orders order) {
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);
        historyService.updateStatusHistory(order);
    }

    public void deleteById(UUID orderId) {
        Orders order = orderService.getOrderById(orderId);
        order.setStatus(OrderStatus.UNACCEPTED);
        orderRepository.save(order);
        historyService.updateStatusHistory(order);
    }
}
