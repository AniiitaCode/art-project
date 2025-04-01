package com.example.art;

import com.example.art.adminBalance.model.Balance;
import com.example.art.adminBalance.repository.AdminBalanceRepository;
import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.adminTransaction.service.AdminTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class GetBalanceITest {


    @Autowired
    private AdminTransactionService adminTransactionService;

    @Autowired
    private AdminBalanceService adminBalanceService;

    @MockBean
    private AdminBalanceRepository adminBalanceRepository;


    @Test
    void whenGetBalance_returnBalance() {

        Balance balance = Balance.builder()
                .balance(new BigDecimal("50.00"))
                .build();

        when(adminBalanceRepository.findFirst()).thenReturn(balance);

        BigDecimal result = adminBalanceService.getBalance();

        assertEquals(new BigDecimal("50.00"), result);

        verify(adminBalanceRepository, times(1)).findFirst();
    }

    @Test
    void whenGetBalance_balanceIsNull_returnNewBalance() {

        when(adminBalanceRepository.findFirst()).thenReturn(null);

        BigDecimal result = adminBalanceService.getBalance();

        assertEquals(new BigDecimal("0"), result);

        verify(adminBalanceRepository, times(1)).findFirst();
    }
}