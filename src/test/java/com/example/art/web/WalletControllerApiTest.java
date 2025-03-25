package com.example.art.web;

import com.example.art.security.AuthenticationDetails;
import com.example.art.transaction.model.Transaction;
import com.example.art.transaction.model.TransactionType;
import com.example.art.transaction.service.TransactionService;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
public class WalletControllerApiTest {

    @MockBean
    private UserService userService;

    @MockBean
    private WalletService walletService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWalletPage() throws Exception {
        UUID userId = UUID.randomUUID();


        User user = User.builder()
                .firstName("Ani")
                .username("ani123")
                .password("123123")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .build();

        Wallet wallet = Wallet
                .builder()
                .balance(new BigDecimal("20.00"))
                .currency("BGN")
                .owner(user)
                .build();

        Transaction transaction1 = Transaction.builder()
                .transactionType(TransactionType.ADD)
                .amount(new BigDecimal("50.00"))
                .build();

        Transaction transaction2 = Transaction.builder()
                .transactionType(TransactionType.ADD)
                .amount(new BigDecimal("50.00"))
                .build();

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);


        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        when(userService.getById(userId)).thenReturn(user);
        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(transactionService.getByWallet(wallet)).thenReturn(transactions);


        MockHttpServletRequestBuilder request = get("/wallets")
                .with(user(authenticationDetails))
                        .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("wallet"))
                .andExpect(model().attributeExists("wallet"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attribute("transactions", transactions));


        verify(userService).getById(userId);
        verify(walletService).getWalletByUser(user);
        verify(transactionService).getByWallet(wallet);
    }
}

