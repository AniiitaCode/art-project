package com.example.art.order.service;

import com.example.art.design.model.*;
import com.example.art.exception.DateAndTimeAlreadyExistException;
import com.example.art.exception.DomainException;
import com.example.art.history.service.HistoryService;
import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import com.example.art.web.dto.OrderRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final HistoryService historyService;

    public OrderService(OrderRepository orderRepository,
                        HistoryService historyService) {
        this.orderRepository = orderRepository;
        this.historyService = historyService;
    }

    public void saveOrder(OrderRequest orderRequest,
                          Design design) {

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        if (orderRequest.getSavedHour().isBefore(startTime) || orderRequest.getSavedHour().isAfter(endTime)) {
            throw new DomainException("Time must be between 10:00 and 18:00!");
        }

        Optional<Orders> isSaved =
                orderRepository.findBySavedDateAndSavedHour(orderRequest.getSavedDate(), orderRequest.getSavedHour());

        if (isSaved.isPresent()) {
            throw new DateAndTimeAlreadyExistException("This date and time are already booked!");
        }

        Orders orders = Orders.builder()
                .savedDate(orderRequest.getSavedDate())
                .savedHour(orderRequest.getSavedHour())
                .design(design)
                .build();


        orderRepository.save(orders);
        historyService.saveInHistory(orders, design.getUser());
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

    public LinkedHashMap<String, BigDecimal> createBill(Design design) {
        LinkedHashMap<String, BigDecimal> bill = new LinkedHashMap<>();
        BigDecimal totalPrice = new BigDecimal("0.00");

        ConstructionDesign constructionDesign = design.getConstruction();
        DecorationPebbles pebbles = design.getPebbles();
        DecorationPicture picture = design.getPicture();

        bill.put("GelPolish", new BigDecimal("20.00"));
        totalPrice = totalPrice.add(new BigDecimal("20.00"));

        if (constructionDesign.equals(ConstructionDesign.YES)) {
            bill.put("Construction", new BigDecimal("20.00"));
            totalPrice = totalPrice.add(new BigDecimal("20.00"));
        }

        if (!pebbles.equals(DecorationPebbles.NONE)) {
            bill.put("Pebbles", new BigDecimal("6.00"));
            totalPrice = totalPrice.add(new BigDecimal("6.00"));
        }

        if (!picture.equals(DecorationPicture.NONE)) {
            bill.put("Picture", new BigDecimal("7.00"));
            totalPrice = totalPrice.add(new BigDecimal("7.00"));
        }

        bill.put("TotalPrice", totalPrice);
        return bill;
    }
}





