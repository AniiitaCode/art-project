package com.example.art.web;

import com.example.art.history.service.HistoryService;
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

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerApiTest {

    @MockBean
    private UserService userService;

    @MockBean
    private HistoryService historyService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getHomeView_shouldReturnsHomePage() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password("123456")
                .username("user")
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        MockHttpServletRequestBuilder request = get("/home")
                .with(user(authenticationDetails))
                .with(csrf());

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void deleteHistory() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);
        UUID historyId = UUID.randomUUID();

        doNothing().when(historyService).deleteById(historyId);

        MockHttpServletRequestBuilder request = delete("/histories/{id}", historyId)
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
