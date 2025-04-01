package com.example.art;

import com.example.art.adminBalance.model.Balance;
import com.example.art.adminBalance.repository.AdminBalanceRepository;
import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.adminTransaction.model.TypeTransaction;
import com.example.art.adminTransaction.service.AdminTransactionService;
import com.example.art.exception.InsufficientBalanceException;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class WithdrawBalanceITest {

    @MockBean
    private AdminTransactionService adminTransactionService;

    @Autowired
    private AdminBalanceService adminBalanceService;

    @MockBean
    private AdminBalanceRepository adminBalanceRepository;

    @Test
    void withdrawBalance_saveInDatabase() {
        User user = User.builder()
                .firstName("Ani")
                .role(UserRole.ADMIN)
                .username("user")
                .password("123456")
                .email("user@abv.bg")
                .build();

        BigDecimal amount = new BigDecimal("100.00");

        Balance balance = Balance.builder()
                .balance(new BigDecimal("200.00"))
                .build();

        when(adminBalanceRepository.findFirst()).thenReturn(balance);

        adminBalanceService.withdrawBalance(user, amount);

        assertEquals(new BigDecimal("100.00"), balance.getBalance());

        verify(adminBalanceRepository, times(1)).save(balance);
        verify(adminTransactionService, times(1))
                .createWithdrawTransaction(user, TypeTransaction.WITHDRAW, amount);
    }

    @Test
    void withdrawBalance_throwException() {
        User user = User.builder()
                .firstName("Ani")
                .role(UserRole.ADMIN)
                .username("user")
                .password("123456")
                .email("user@abv.bg")
                .build();

        BigDecimal amount = new BigDecimal("100.00");

        Balance balance = Balance.builder()
                .balance(new BigDecimal("50.00"))
                .build();

        when(adminBalanceRepository.findFirst()).thenReturn(balance);

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            adminBalanceService.withdrawBalance(user, amount);
        });

        assertEquals("Your balance is insufficient.", exception.getMessage());
    }
}
