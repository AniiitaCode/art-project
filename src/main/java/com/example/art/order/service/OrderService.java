package com.example.art.order.service;

import com.example.art.design.model.Design;
import com.example.art.exception.DomainException;
import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import com.example.art.web.dto.OrderRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrder(OrderRequest orderRequest, Design design) {

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        if (orderRequest.getSavedHour().isBefore(startTime) || orderRequest.getSavedHour().isAfter(endTime)) {
            throw new DomainException("Time must be between 10:00 and 18:00!");
        }

        Optional<Orders> isSaved =
                orderRepository.findBySavedDateAndSavedHour(orderRequest.getSavedDate(), orderRequest.getSavedHour());

        if (isSaved.isPresent()) {
            throw new DomainException("This date and time are already booked!");
        }

        Orders orders = Orders.builder()
                .savedDate(orderRequest.getSavedDate())
                .savedHour(orderRequest.getSavedHour())
                .design(design)
                .build();

        orderRepository.save(orders);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Orders::getSavedDate)
                        .thenComparing(Orders::getSavedHour))
                .toList();
    }


    public Orders getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException("Order with id [%s] does not exist."
                        .formatted(orderId)));
    }
}





