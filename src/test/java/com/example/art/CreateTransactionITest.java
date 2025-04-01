package com.example.art;

import com.example.art.adminTransaction.model.AdminTransaction;
import com.example.art.adminTransaction.model.TypeTransaction;
import com.example.art.adminTransaction.repository.AdminTransactionRepository;
import com.example.art.adminTransaction.service.AdminTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CreateTransactionITest {

    @Autowired
    private AdminTransactionRepository adminTransactionRepository;

    @Autowired
    private AdminTransactionService adminTransactionService;

    @Test
    void createAddTransaction_saveInDatabase() {
        TypeTransaction typeTransaction = TypeTransaction.ADD;
        BigDecimal totalPrice = new BigDecimal("100.00");
        String username = "testUser";

        adminTransactionService.createAddTransaction(typeTransaction, totalPrice, username);

        List<AdminTransaction> transactions = adminTransactionRepository.findByUsername(username);

        assertNotNull(transactions);
        assertThat(transactions.size()).isGreaterThan(0);

        AdminTransaction savedTransaction = transactions.get(transactions.size() - 1);
        assertThat(savedTransaction.getAmount()).isEqualByComparingTo(totalPrice);
        assertThat(savedTransaction.getTypeTransaction()).isEqualTo(typeTransaction);
        assertThat(savedTransaction.getUsername()).isEqualTo(username);
        assertThat(savedTransaction.getTransactionDate()).isNotNull();
    }
}
