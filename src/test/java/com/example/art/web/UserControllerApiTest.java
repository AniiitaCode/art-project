package com.example.art.web;

import com.example.art.exception.DomainException;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.service.UserService;
import com.example.art.wallet.model.Wallet;
import com.example.art.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockBean
    private UserService userService;

    @Captor
    ArgumentCaptor<UserEditRequest> argumentCaptor;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUpdateUserProfile_shouldReturnsUpdateProfile() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/users/profile/edit")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("profile-edit"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("userEditRequest"));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void putUpdateUserProfile_hasErrors() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/profile/edit")
                .formField("firstName", "Ani")
                .formField("email", "")
                .formField("profilePicture", "www.image.com")
                .formField("username", "ani123")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(view().name("profile-edit"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("userEditRequest"));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void putUpdateUserProfile_happyPath() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);


        MockHttpServletRequestBuilder request = put("/users/profile/edit")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(redirectedUrl("/home"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void putSwitchRoleUser_happyPath() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.ADMIN)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        when(userService.getById(userId)).thenReturn(user);
        doNothing().when(userService).switchRole(userId);

        MockHttpServletRequestBuilder request = put("/users/{userId}/role", userId)
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(redirectedUrl("/users"))
                .andExpect(status().is3xxRedirection());


        verify(userService, times(1)).getById(userId);
        verify(userService, times(1)).switchRole(userId);
    }

    @Test
    void putSwitchRoleUser_hasError() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.USER);

        User user = User.builder()
                .id(userId)
                .firstName("Ani")
                .email("ani@abv.bg")
                .role(UserRole.USER)
                .password(authenticationDetails.getPassword())
                .username(authenticationDetails.getUsername())
                .wallet(new Wallet())
                .profilePicture("www.image.com")
                .build();

        MockHttpServletRequestBuilder request = put("/users/{userId}/role", userId)
                .with(user(authenticationDetails))
                .with(csrf());

        when(userService.getById(userId)).thenReturn(user);
        doThrow(DomainException.class).when(userService).switchRole(userId);

        mockMvc.perform(request);

        verify(userService, never()).switchRole(userId);
    }

    @Test
    void getAllUsers() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationDetails authenticationDetails =
                new AuthenticationDetails(userId, "user", "123456", UserRole.ADMIN);

        MockHttpServletRequestBuilder request = get("/users")
                .with(user(authenticationDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));
    }
}
