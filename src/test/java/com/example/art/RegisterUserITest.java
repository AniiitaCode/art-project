package com.example.art;

import com.example.art.email.service.EmailService;
import com.example.art.user.model.User;
import com.example.art.user.repository.UserRepository;
import com.example.art.user.service.UserService;
import com.example.art.wallet.service.WalletService;
import com.example.art.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class RegisterUserITest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;


    @Test
    void whenNewUserRegister() {
        UUID userId = UUID.randomUUID();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Ani");
        registerRequest.setUsername("ani123");
        registerRequest.setEmail("ani@abv.bg");
        registerRequest.setPassword("123123");
        registerRequest.setConfirmPassword("123123");

        User user = User.builder()
                .id(userId)
                .firstName(registerRequest.getFirstName())
                .email(registerRequest.getEmail())
                .createdOn(LocalDateTime.now())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();


       userService.register(registerRequest);

       emailService.savePreference(userId, true, user.getEmail());

       emailService.sendEmail(userId, "test", "test");

       assertTrue(userRepository.count() > 0);
       verify(emailService, times(1)).sendEmail(userId, "test", "test");
       verify(emailService, times(1)).savePreference(userId, true, user.getEmail());
    }
}
