package com.example.art;

import com.example.art.adminBalance.model.Balance;
import com.example.art.adminBalance.repository.AdminBalanceRepository;
import com.example.art.adminBalance.service.AdminBalanceService;
import com.example.art.adminTransaction.service.AdminTransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
public class AddBalanceITest {

    @Autowired
    private AdminTransactionService adminTransactionService;

    @Autowired
    private AdminBalanceService adminBalanceService;

    @MockBean
    private AdminBalanceRepository adminBalanceRepository;

    @Test
    void addBalance_saveBalanceInDatabase() {
        Balance balance = Balance.builder()
                .balance(new BigDecimal("50.00"))
                .build();

        when(adminBalanceRepository.findFirst()).thenReturn(balance);

        BigDecimal amount = new BigDecimal("50.00");

        adminBalanceService.addBalance(amount);

        assertEquals(new BigDecimal("100.00"), balance.getBalance());

        verify(adminBalanceRepository, times(1)).findFirst();
        verify(adminBalanceRepository, times(1)).save(balance);
    }

    @Test
    void whenAddBalance_balanceIsNull_returnNewBalance() {

        when(adminBalanceRepository.findFirst()).thenReturn(null);

        adminBalanceService.addBalance(new BigDecimal("50.00"));

        verify(adminBalanceRepository, times(1)).findFirst();
        verify(adminBalanceRepository, times(2)).save(any(Balance.class));

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(adminBalanceRepository, times(2)).save(balanceCaptor.capture());

        assertEquals(new BigDecimal("50.00"), balanceCaptor.getValue().getBalance());
    }
}
