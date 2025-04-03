package com.example.art.web;

import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.order.model.Orders;
import com.example.art.order.service.OrderService;
import com.example.art.schedule.service.ScheduleService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.transaction.model.TransactionType;
import com.example.art.transaction.service.TransactionService;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ScheduleController.class)
public class ScheduleControllerApiTest {

    @MockBean
    private OrderService orderService;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private UserService userService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AdminBalanceService adminBalanceService;

    @MockBean
    private WalletService walletService;

    @Captor
    private ArgumentCaptor<BigDecimal> amountCaptor;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getScheduleView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.ADMIN);

        MockHttpServletRequestBuilder request = get("/schedule")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("schedule"))
                .andExpect(model().attributeExists("allOrders"));
    }

    @Test
    void postAcceptOrder() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.ADMIN);

        UUID orderId = UUID.randomUUID();
        MockHttpServletRequestBuilder request = post("/schedule/{orderId}/confirm", orderId)
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                        .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/schedule"));
    }

    @Test
    void deleteOrder() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, "user", "123456", UserRole.ADMIN);

        User user = mock(User.class);
        when(user.getId()).thenReturn(authenticationDetails.getUserId());
        when(user.getFirstName()).thenReturn("Ani");
        when(user.getEmail()).thenReturn("ani@abv.bg");
        when(user.getUsername()).thenReturn(authenticationDetails.getUsername());
        when(user.getPassword()).thenReturn(authenticationDetails.getPassword());
        when(user.getRole()).thenReturn(authenticationDetails.getRole());

        UUID orderId = UUID.randomUUID();
        Orders order = mock(Orders.class);
        when(order.getUser()).thenReturn(user);

        Wallet wallet = mock(Wallet.class);
        when(user.getWallet()).thenReturn(wallet);

        BigDecimal amount = new BigDecimal("100.00");

        LinkedHashMap<String, BigDecimal> bill = new LinkedHashMap<>();
        bill.put("TotalPrice", amount);
        when(orderService.createBill(order.getDesign())).thenReturn(bill);

        doNothing().when(adminBalanceService).withdrawBalance(user, amount);
        doNothing().when(transactionService)
                .createTransaction(any(User.class), any(Wallet.class), eq(new BigDecimal("100.00")), eq(TransactionType.ADD));

        when(walletService.addBalance(user, amount)).thenReturn(wallet);
        doNothing().when(scheduleService).deleteById(orderId);
        when(orderService.getOrderById(orderId)).thenReturn(order);
        when(order.getUser()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);


        MockHttpServletRequestBuilder request = post("/schedule/{orderId}", orderId)
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule"))
                        .andDo(print());

        verify(transactionService, times(1)).createTransaction(any(User.class), any(Wallet.class), amountCaptor.capture(), eq(TransactionType.ADD));
        verify(orderService, times(1)).getOrderById(orderId);
        verify(walletService, times(1)).addBalance(user, amount);
        verify(scheduleService, times(1)).deleteById(orderId);
    }
}

