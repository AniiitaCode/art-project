package com.example.art.web;

import com.example.art.email.client.dto.EmailPreference;
import com.example.art.email.service.EmailService;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
public class EmailControllerApiTest {

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPreferenceView_shouldReturnPreferencePage() throws Exception {

        Wallet wallet = Wallet.builder()
                .currency("BGN")
                .balance(new BigDecimal("100"))
                .build();

        User user = User.builder()
                .firstName("Ani")
                .username("ani123")
                .password("123123")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .wallet(wallet)
                .build();


        EmailPreference emailPreference = new EmailPreference();
        emailPreference.setEnabled(true);
        emailPreference.setContactEmail("ani@abv.bg");


        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123123", UserRole.USER);

        when(emailService.getPreference(user.getId())).thenReturn(emailPreference);
        when(userService.getById(userId)).thenReturn(user);


        mockMvc.perform(get("/emails")
                        .with(user(authenticationDetails))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("email"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("emailPreference"))
                .andExpect(model().attribute("emailPreference", emailPreference));


        verify(userService, times(1)).getById(userId);
        verify(emailService, times(1)).getPreference(user.getId());
    }

    @Test
    void putChangePreference_shouldReturnsChangePreference() throws Exception {

        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123123", UserRole.USER);

        doNothing().when(emailService).changePreference(userId, true);

        MockHttpServletRequestBuilder request = put("/emails/preference")
                .param("enabled", "true")
                .with(user(authenticationDetails))
                .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/emails"));

        verify(emailService, times(1)).changePreference(userId, true);
    }
}





