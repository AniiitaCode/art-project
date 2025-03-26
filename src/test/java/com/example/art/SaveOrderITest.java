package com.example.art;

import com.example.art.design.model.*;
import com.example.art.design.repository.DesignRepository;
import com.example.art.exception.DateAndTimeAlreadyExistException;
import com.example.art.exception.DomainException;
import com.example.art.order.model.Orders;
import com.example.art.order.model.PaymentType;
import com.example.art.order.repository.OrderRepository;
import com.example.art.order.service.OrderService;
import com.example.art.web.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class SaveOrderITest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DesignRepository designRepository;

    @Test
    void whenSaveOrder_validTime_shouldSaveOrder() {
        LocalDate savedDate = LocalDate.now().plusDays(1);
        LocalTime savedHour = LocalTime.of(12, 0);

        Design design = Design.builder()
                .color("03")
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.BEAR)
                .construction(ConstructionDesign.YES)
                .form(FormDesign.ALMOND)
                .build();

        designRepository.save(design);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setSavedHour(savedHour);
        orderRequest.setSavedDate(savedDate);
        orderRequest.setDesign(design);
        orderRequest.setPaymentType(PaymentType.IN_SALON);


        orderService.saveOrder(orderRequest, design);

        Optional<Orders> savedOrder =
                orderRepository.findBySavedDateAndSavedHour(savedDate, savedHour);
        assertTrue(savedOrder.isPresent());
    }

    @Test
    public void testSaveOrder_InvalidTime_ShouldThrowDomainException() {
        LocalDate savedDate = LocalDate.now().plusDays(1);
        LocalTime savedHour = LocalTime.of(9, 0);

        Design design = Design.builder()
                .color("03")
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.BEAR)
                .construction(ConstructionDesign.YES)
                .form(FormDesign.ALMOND)
                .build();

        designRepository.save(design);


        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setDesign(design);
        orderRequest.setSavedHour(savedHour);
        orderRequest.setSavedDate(savedDate);

        DomainException message = assertThrows(DomainException.class, () -> {
            orderService.saveOrder(orderRequest, design);
        });

        assertEquals("Time must be between 10:00 and 18:00!", message.getMessage());
    }

    @Test
    public void testSaveOrder_AlreadyBookedTime_ShouldThrowDateAndTimeAlreadyExistException() {
        LocalDate savedDate = LocalDate.now().plusDays(1);
        LocalTime savedHour = LocalTime.of(12, 0);

        Design design = Design.builder()
                .color("03")
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.BEAR)
                .construction(ConstructionDesign.YES)
                .form(FormDesign.ALMOND)
                .build();

        designRepository.save(design);


        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setSavedDate(savedDate);
        orderRequest.setSavedHour(savedHour);
        orderRequest.setDesign(design);
        orderRequest.setPaymentType(PaymentType.IN_SALON);

        orderService.saveOrder(orderRequest, design);

        DateAndTimeAlreadyExistException exception = assertThrows(DateAndTimeAlreadyExistException.class, () -> {
            orderService.saveOrder(orderRequest, design);
        });

        assertEquals("This date and time are already booked!", exception.getMessage());
    }

    @Test
    void testSaveOrder_shouldSavedToDatabase() {
        LocalDate savedDate = LocalDate.now().plusDays(1);
        LocalTime savedHour = LocalTime.of(12, 0);

        Design design = Design.builder()
                .color("03")
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.BEAR)
                .construction(ConstructionDesign.YES)
                .form(FormDesign.ALMOND)
                .build();

        designRepository.save(design);


        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setSavedDate(savedDate);
        orderRequest.setSavedHour(savedHour);
        orderRequest.setDesign(design);
        orderRequest.setPaymentType(PaymentType.IN_SALON);

        orderService.saveOrder(orderRequest, design);

        Orders orders =
                orderRepository.findBySavedDateAndSavedHour(savedDate, savedHour)
                        .orElse(null);

        assertNotNull(orders);
        assertEquals(savedDate, orders.getSavedDate());
        assertEquals(savedHour, orders.getSavedHour());
    }
}

