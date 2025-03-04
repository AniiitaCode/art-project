package com.example.art.schedule.service;

import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ScheduleService {

    private final OrderRepository orderRepository;

    public ScheduleService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void acceptOrder(Orders order) {
        order.setAccept(true);
        orderRepository.save(order);
    }

    public void deleteById(UUID orderId) {
        orderRepository.deleteById(orderId);
    }
}
