package com.example.art;

import com.example.art.email.service.EmailService;
import com.example.art.exception.DomainException;
import com.example.art.exception.UsernameAlreadyExistException;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.repository.UserRepository;
import com.example.art.user.service.UserService;
import com.example.art.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSwitchRole_userNotFound() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> userService.switchRole(userId));
    }


    @Test
    public void testSwitchRole_userIsAlreadyAdmin() {

        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(existingUser));

        assertThrows(DomainException.class, () -> userService.switchRole(userId));
    }


    @Test
    public void testSwitchRole_Success() {

        UUID userId = UUID.randomUUID();
        User normalUser = User.builder()
                        .id(userId)
                        .role(UserRole.USER)
                        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(normalUser));

        userService.switchRole(userId);

        verify(userRepository).save(argThat(user -> user.getRole() == UserRole.ADMIN));
    }


    @Test
    public void testRegister_userAlreadyExist() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("user");
        registerRequest.setEmail("existing_user@example.com");
        registerRequest.setPassword("123123");

        User excitingUser = User.builder()
                .username("user")
                .email("existing_user@example.com")
                .password("123123")
                .build();

        when(userRepository.findByUsernameOrEmail("user", "existing_user@example.com"))
                .thenReturn(Optional.of(excitingUser));

        UsernameAlreadyExistException exception = assertThrows(UsernameAlreadyExistException.class, ()
        -> userService.register(registerRequest));

        assertEquals("This username or email already exist.", exception.getMessage());
    }
}



