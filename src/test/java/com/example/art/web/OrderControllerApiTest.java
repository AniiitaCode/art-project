package com.example.art.web;

import com.example.art.design.model.*;
import com.example.art.design.service.DesignService;
import com.example.art.order.model.PaymentType;
import com.example.art.order.service.OrderService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.web.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerApiTest {

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private DesignService designService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOrderView_shouldReturnsOrderPage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = get("/orders")
                .with(user(authenticationDetails))
                .with(csrf());

        Design design = Design.builder()
                .color("02")
                .form(FormDesign.ALMOND)
                .construction(ConstructionDesign.NO)
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.BEAR)
                .build();

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .designs(List.of(design))
                .build();


        LinkedHashMap<String, BigDecimal> bill = new LinkedHashMap<>();
        bill.put("GelPolish", new BigDecimal("20.00"));
        bill.put("TotalPrice", new BigDecimal("20.00"));


        when(userService.getById(userId)).thenReturn(user);
        when(designService.getLastDesign(userId)).thenReturn(design);
        when(orderService.createBill(design)).thenReturn(bill);


        mockMvc.perform(request)  // Мокваме principal
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("design", design))
                .andExpect(model().attributeExists("orderRequest"))
                .andExpect(model().attribute("bill", bill));

        verify(userService, times(1)).getById(userId);
        verify(designService, times(1)).getLastDesign(userId);
    }

    @Test
    void postOrderView_happyPath() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        Design design = Design.builder()
                .color("02")
                .form(FormDesign.ALMOND)
                .construction(ConstructionDesign.NO)
                .pebbles(DecorationPebbles.DIAMONDS)
                .picture(DecorationPicture.BEAR)
                .build();

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .designs(List.of(design))
                .build();

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setDesign(design);
        orderRequest.setSavedDate(LocalDate.of(2025, 12, 10));
        orderRequest.setSavedHour(LocalTime.of(10, 0));


        when(userService.getById(userId)).thenReturn(user);
        when(designService.getLastDesign(userId)).thenReturn(design);
        doNothing().when(orderService).saveOrder(any(OrderRequest.class), any(Design.class));


        MockHttpServletRequestBuilder request = post("/orders")
                .param("savedDate", "2025-12-10")
                .param("savedHour", "10:00")
                .param("paymentType", String.valueOf(PaymentType.WALLET))
                .with(user(authenticationDetails))
                .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, times(1)).getById(userId);
        verify(designService, times(1)).getLastDesign(userId);
        verify(orderService, times(1)).saveOrder(any(OrderRequest.class), any(Design.class));
    }
}