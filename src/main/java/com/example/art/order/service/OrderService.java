package com.example.art.order.service;

import com.example.art.design.model.*;
import com.example.art.exception.*;
import com.example.art.history.service.HistoryService;
import com.example.art.order.model.OrderStatus;
import com.example.art.order.model.Orders;
import com.example.art.order.model.PaymentType;
import com.example.art.order.repository.OrderRepository;
import com.example.art.wallet.service.WalletService;
import com.example.art.web.dto.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final HistoryService historyService;
    private final WalletService walletService;

    public OrderService(OrderRepository orderRepository,
                        HistoryService historyService,
                        WalletService walletService) {
        this.orderRepository = orderRepository;
        this.historyService = historyService;
        this.walletService = walletService;
    }

    @Transactional
    public void saveOrder(OrderRequest orderRequest,
                          Design design) {

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        if (orderRequest.getSavedHour().isBefore(startTime) || orderRequest.getSavedHour().isAfter(endTime)) {
            throw new InvalidTimeException("Time must be between 10:00 and 18:00!");
        }


        if (orderRequest.getSavedDate().isBefore(LocalDate.now())) {
            throw new DateMustBeInFutureException("The order date must be in the future.");
        }

        Optional<Orders> isSaved =
                orderRepository.findBySavedDateAndSavedHour(orderRequest.getSavedDate(), orderRequest.getSavedHour());

        if (isSaved.isPresent()) {
            throw new DateAndTimeAlreadyExistException("This date and time are already booked!");
        }

        if (orderRequest.getPaymentType().equals(PaymentType.WALLET)) {
            LinkedHashMap<String, BigDecimal> bill = createBill(design);


            walletService.withdrawBalance(design.getUser(), bill.get("TotalPrice"));
        }

        Orders orders = Orders.builder()
                .savedDate(orderRequest.getSavedDate())
                .savedHour(orderRequest.getSavedHour())
                .status(OrderStatus.PENDING)
                .design(design)
                .user(design.getUser())
                .paymentType(orderRequest.getPaymentType())
                .build();


        orderRepository.save(orders);
        historyService.saveInHistory(orders, design.getUser());
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus() != OrderStatus.UNACCEPTED)
                .sorted(Comparator.comparing(Orders::getSavedDate)
                        .thenComparing(Orders::getSavedHour))
                .toList();
    }


    public Orders getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id [%s] does not exist."
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





