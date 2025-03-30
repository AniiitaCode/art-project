package com.example.art.web;

import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.adminTransaction.model.AdminTransaction;
import com.example.art.adminTransaction.model.TypeTransaction;
import com.example.art.adminTransaction.service.AdminTransactionService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminBalanceController.class)
public class AdminBalanceControllerApiTest {

    @MockBean
    private AdminBalanceService adminBalanceService;

    @MockBean
    private AdminTransactionService adminTransactionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBalanceView_thenReturnBalancePage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.ADMIN);

        AdminTransaction transaction1 = AdminTransaction.builder()
                .typeTransaction(TypeTransaction.ADD)
                .amount(new BigDecimal("50.00"))
                .build();

        AdminTransaction transaction2 = AdminTransaction.builder()
                .typeTransaction(TypeTransaction.ADD)
                .amount(new BigDecimal("50.00"))
                .build();

        BigDecimal balance = new BigDecimal("100.00");
        List<AdminTransaction> transactions = List.of(transaction1, transaction2);

        when(adminTransactionService.getAllTransaction()).thenReturn(transactions);
        when(adminBalanceService.getBalance()).thenReturn(balance);

        MockHttpServletRequestBuilder request = get("/balance")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("balance"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("balance"))
                .andExpect(model().attribute("balance", balance))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attribute("transactions", transactions));

        verify(adminBalanceService, times(1)).getBalance();
        verify(adminTransactionService, times(1)).getAllTransaction();
    }

    @Test
    void whenBalancePage_userRoleUser_returnForbiddenException() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = get("/balance")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTransactionHistory() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.ADMIN);

        AdminTransaction transaction1 = AdminTransaction.builder()
                .typeTransaction(TypeTransaction.ADD)
                .amount(new BigDecimal("50.00"))
                .build();

        AdminTransaction transaction2 = AdminTransaction.builder()
                .typeTransaction(TypeTransaction.ADD)
                .amount(new BigDecimal("50.00"))
                .build();

        List<AdminTransaction> transactions = List.of(transaction1, transaction2);

        when(adminTransactionService.getAllTransaction()).thenReturn(transactions);


        MockHttpServletRequestBuilder request = delete("/balance/transactions/clear")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/balance"));

        verify(adminTransactionService, times(1)).getAllTransaction();
        verify(adminTransactionService, times(1)).clearHistory(transactions);
    }

    @Test
    void whenClearTransactionHistory_userRoleUser_returnForbiddenException() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        MockHttpServletRequestBuilder request = delete("/balance/transactions/clear")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

}
