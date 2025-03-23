package com.example.art.web;

import com.example.art.order.service.OrderService;
import com.example.art.schedule.service.ScheduleService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.UserRole;
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

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getScheduleView() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = get("/schedule")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("schedule"))// Проверяваме дали изгледът е "schedule"
                .andExpect(model().attributeExists("allOrders"));
    }

    @Test
    void postAcceptOrder() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

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
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        UUID orderId = UUID.randomUUID();
        MockHttpServletRequestBuilder request = delete("/schedule/{orderId}", orderId)
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule"));
    }
}

