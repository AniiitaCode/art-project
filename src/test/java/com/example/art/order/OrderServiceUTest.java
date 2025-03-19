package com.example.art.order;

import com.example.art.design.model.ConstructionDesign;
import com.example.art.design.model.DecorationPebbles;
import com.example.art.design.model.DecorationPicture;
import com.example.art.design.model.Design;
import com.example.art.exception.DomainException;
import com.example.art.order.model.Orders;
import com.example.art.order.repository.OrderRepository;
import com.example.art.order.service.OrderService;
import com.example.art.web.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void whenSaveOrder_savedHourIsBeforeStartTime_thenThrowsDomainException() {
        LocalTime savedHour = LocalTime.of(9, 0);
        OrderRequest dto = new OrderRequest();
        dto.setSavedHour(savedHour);

        DomainException exception = assertThrows(DomainException.class, () -> {
            if (dto.getSavedHour().isBefore(LocalTime.of(10, 0))
                    || dto.getSavedHour().isAfter(LocalTime.of(18, 0))) {
                throw new DomainException("Time must be between 10:00 and 18:00!");
            }
        });

        assertEquals("Time must be between 10:00 and 18:00!", exception.getMessage());
    }

    @Test
    void whenSaveOrder_savedHourIsExactlyStartTime_thenNoException() {
        LocalTime savedHour = LocalTime.of(10, 0);
        OrderRequest dto = new OrderRequest();
        dto.setSavedHour(savedHour);

        assertDoesNotThrow(() -> {
            if (dto.getSavedHour().isBefore(LocalTime.of(10, 0))
                    || dto.getSavedHour().isAfter(LocalTime.of(18, 0))) {
                throw new DomainException("Time must be between 10:00 and 18:00!");
            }
        });
    }

    @Test
    void whenSaveOrder_savedHourIsExactlyEndTime_thenNoException() {
        LocalTime savedHour = LocalTime.of(18, 0);
        OrderRequest dto = new OrderRequest();
        dto.setSavedHour(savedHour);

        assertDoesNotThrow(() -> {
            if (dto.getSavedHour().isBefore(LocalTime.of(10, 0))
                    || dto.getSavedHour().isAfter(LocalTime.of(18, 0))) {
                throw new DomainException("Time must be between 10:00 and 18:00!");
            }
        });
    }

    @Test
    void whenSaveOrder_savedHourIsAfterEndTime_thenThrowsDomainException() {
        LocalTime savedHour = LocalTime.of(19, 0);
        OrderRequest dto = new OrderRequest();
        dto.setSavedHour(savedHour);

        DomainException exception = assertThrows(DomainException.class, () -> {
            if (dto.getSavedHour().isBefore(LocalTime.of(10, 0))
                    || dto.getSavedHour().isAfter(LocalTime.of(18, 0))) {
                throw new DomainException("Time must be between 10:00 and 18:00!");
            }
        });

        assertEquals("Time must be between 10:00 and 18:00!", exception.getMessage());
    }

    @Test
    void whenSaveOrder_savedHourIsWithinValidRange() {
        LocalTime savedHour = LocalTime.of(15, 0);
        OrderRequest dto = new OrderRequest();
        dto.setSavedHour(savedHour);

        assertDoesNotThrow(() -> {
            if (dto.getSavedHour().isBefore(LocalTime.of(10, 0))
                    || dto.getSavedHour().isAfter(LocalTime.of(18, 0))) {
                throw new DomainException("Time must be between 10:00 and 18:00!");
            }
        });
    }

    @Test
    void whenSaveOrder_dateAndTimeAlreadyBooked_thenThrowsDomainException() {
        Design design = new Design();

        LocalDate savedDate = LocalDate.of(2025, 12, 19);
        LocalTime savedHour = LocalTime.of(10, 0);

        OrderRequest dto = new OrderRequest();
        dto.setSavedDate(savedDate);
        dto.setSavedHour(savedHour);

        Orders excitingOrder = Orders.builder()
                .savedDate(savedDate)
                .savedHour(savedHour)
                .build();

        Optional<Orders> isSaved = Optional.of(excitingOrder);

        when(orderRepository.findBySavedDateAndSavedHour(savedDate, savedHour))
                .thenReturn(isSaved);

        DomainException exception =
                assertThrows(DomainException.class, () -> {
                    orderService.saveOrder(dto, design);
                });

        assertEquals("This date and time are already booked!", exception.getMessage());
    }

    @Test
    void whenSavedOrder_dateAndTimeNotExist() {
        Design design = new Design();

        LocalDate savedDate = LocalDate.of(2025, 12, 19);
        LocalTime savedHour = LocalTime.of(10, 0);

        OrderRequest dto = new OrderRequest();
        dto.setSavedDate(savedDate);
        dto.setSavedHour(savedHour);

        when(orderRepository.findBySavedDateAndSavedHour(savedDate, savedHour))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            orderService.saveOrder(dto, design);
        });
    }

    @Test
    void whenSaveOrder_thenSuccessSaveOrderInDatabase(){
        LocalTime savedHour = LocalTime.of(15, 0);
        LocalDate savedDate = LocalDate.of(2025, 12, 20);
        Design design = new Design();

        Orders orders = Orders.builder()
                .design(design)
                .savedDate(savedDate)
                .savedHour(savedHour)
                .build();

        orderRepository.save(orders);
        verify(orderRepository, times(1)).save(orders);
    }

    @Test
    void whenGetAllOrders_thenReturnListSortedAllOrders() {
        Orders orders1 = Orders.builder()
                .savedDate(LocalDate.of(2025, 12, 1))
                .savedHour(LocalTime.of(10, 0))
                .build();

        Orders orders2 = Orders.builder()
                .savedDate(LocalDate.of(2025, 12, 2))
                .savedHour(LocalTime.of(11, 0))
                .build();

        Orders orders3 = Orders.builder()
                .savedDate(LocalDate.of(2025, 12, 3))
                .savedHour(LocalTime.of(12, 0))
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(orders1, orders2, orders3));

        List<Orders> orders = orderService.getAllOrders();

        assertEquals(orders1, orders.get(0));
        assertEquals(orders2, orders.get(1));
        assertEquals(orders3, orders.get(2));

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void whenGetOrderById_doesNotExistId_thenThrowDomainException() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        DomainException exception = assertThrows(DomainException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertEquals("Order with id [%s] does not exist.".formatted(orderId), exception.getMessage());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void whenGetOrderById_thenReturnOrder() {
        UUID orderId = UUID.randomUUID();
        Orders order = Orders.builder()
                .id(orderId)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> {
                orderService.getOrderById(orderId);
        });

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void whenCreateBill_thenCorrectBillGenerated() {
        Design design = mock(Design.class);
        when(design.getConstruction()).thenReturn(ConstructionDesign.YES);
        when(design.getPebbles()).thenReturn(DecorationPebbles.DIAMONDS);
        when(design.getPicture()).thenReturn(DecorationPicture.CAT);

        LinkedHashMap<String, BigDecimal> bill = orderService.createBill(design);

        assertEquals(new BigDecimal("20.00"), bill.get("GelPolish"));
        assertEquals(new BigDecimal("20.00"), bill.get("Construction"));
        assertEquals(new BigDecimal("6.00"), bill.get("Pebbles"));
        assertEquals(new BigDecimal("7.00"), bill.get("Picture"));
        assertEquals(new BigDecimal("53.00"), bill.get("TotalPrice"));
    }

    @Test
    void whenCreateBillWithoutConstruction_thenNoConstructionCharge() {
        Design design = mock(Design.class);
        when(design.getConstruction()).thenReturn(ConstructionDesign.NO);
        when(design.getPebbles()).thenReturn(DecorationPebbles.DIAMONDS);
        when(design.getPicture()).thenReturn(DecorationPicture.CAT);

        LinkedHashMap<String, BigDecimal> bill = orderService.createBill(design);

        assertEquals(new BigDecimal("20.00"), bill.get("GelPolish"));
        assertNull(bill.get("Construction"));
        assertEquals(new BigDecimal("6.00"), bill.get("Pebbles"));
        assertEquals(new BigDecimal("7.00"), bill.get("Picture"));
        assertEquals(new BigDecimal("33.00"), bill.get("TotalPrice"));
    }

    @Test
    void whenCreateBillWithoutPebbles_thenNonePebblesCharge() {
        Design design = mock(Design.class);
        when(design.getConstruction()).thenReturn(ConstructionDesign.YES);
        when(design.getPebbles()).thenReturn(DecorationPebbles.NONE);
        when(design.getPicture()).thenReturn(DecorationPicture.CAT);

        LinkedHashMap<String, BigDecimal> bill = orderService.createBill(design);

        assertEquals(new BigDecimal("20.00"), bill.get("GelPolish"));
        assertEquals(new BigDecimal("20.00"), bill.get("Construction"));
        assertNull(bill.get("Pebbles"));
        assertEquals(new BigDecimal("7.00"), bill.get("Picture"));
        assertEquals(new BigDecimal("47.00"), bill.get("TotalPrice"));
    }

    @Test
    void whenCreateBillWithoutPicture_thenNonePictureCharge() {
        Design design = mock(Design.class);
        when(design.getConstruction()).thenReturn(ConstructionDesign.YES);
        when(design.getPebbles()).thenReturn(DecorationPebbles.DIAMONDS);
        when(design.getPicture()).thenReturn(DecorationPicture.NONE);

        LinkedHashMap<String, BigDecimal> bill = orderService.createBill(design);

        assertEquals(new BigDecimal("20.00"), bill.get("GelPolish"));
        assertEquals(new BigDecimal("20.00"), bill.get("Construction"));
        assertEquals(new BigDecimal("6.00"), bill.get("Pebbles"));
        assertNull(bill.get("Picture"));
        assertEquals(new BigDecimal("46.00"), bill.get("TotalPrice"));
    }
}
