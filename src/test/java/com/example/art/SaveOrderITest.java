package com.example.art;

import com.example.art.design.model.*;
import com.example.art.design.repository.DesignRepository;
import com.example.art.exception.InvalidTimeException;
import com.example.art.order.repository.OrderRepository;
import com.example.art.order.service.OrderService;
import com.example.art.wallet.repository.WalletRepository;
import com.example.art.web.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class SaveOrderITest {

    @Autowired
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Autowired
    private DesignRepository designRepository;

    @Autowired
    private WalletRepository walletRepository;


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

        InvalidTimeException message = assertThrows(InvalidTimeException.class, () -> {
            orderService.saveOrder(orderRequest, design);
        });

        assertEquals("Time must be between 10:00 and 18:00!", message.getMessage());
    }
}

