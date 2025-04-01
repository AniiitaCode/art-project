package com.example.art;

import com.example.art.adminTransaction.model.AdminTransaction;
import com.example.art.adminTransaction.model.TypeTransaction;
import com.example.art.adminTransaction.repository.AdminTransactionRepository;
import com.example.art.adminTransaction.service.AdminTransactionService;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CreateWithdrawTransactionITest {

    @Autowired
    private AdminTransactionRepository adminTransactionRepository;

    @Autowired
    private AdminTransactionService adminTransactionService;

    @Test
    void createWithdrawTransaction_saveInDatabase() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("user@abv.bg")
                .password("123456")
                .username("user")
                .role(UserRole.ADMIN)
                .build();

        TypeTransaction typeTransaction = TypeTransaction.WITHDRAW;
        BigDecimal amount = new BigDecimal("100.00");

        adminTransactionService.createWithdrawTransaction(user, typeTransaction, amount);

        List<AdminTransaction> transactions = adminTransactionRepository.findByUsername(user.getUsername());

        assertNotNull(transactions);
        assertThat(transactions.size()).isGreaterThan(0);

        AdminTransaction savedTransaction = transactions.get(transactions.size() - 1);
        assertThat(savedTransaction.getTypeTransaction()).isEqualTo(typeTransaction);
        assertThat(savedTransaction.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedTransaction.getAmount()).isEqualByComparingTo(amount);
    }
}
