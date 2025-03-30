package com.example.art.web;

import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.order.service.OrderService;
import com.example.art.schedule.service.ScheduleService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.transaction.service.TransactionService;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
}

