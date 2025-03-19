package com.example.art.user;

import com.example.art.email.service.EmailService;
import com.example.art.exception.DomainException;
import com.example.art.exception.UsernameAlreadyExistException;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.repository.UserRepository;
import com.example.art.user.service.UserService;
import com.example.art.wallet.service.WalletService;
import com.example.art.web.dto.RegisterRequest;
import com.example.art.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private UserService userService;

    @Test
     void whenSwitchRole_thenUserNotFound() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

       DomainException exception =
               assertThrows(DomainException.class, () -> userService.switchRole(userId));

       assertEquals("User with id [%s] does not exist.".formatted(userId),
               exception.getMessage());
    }


    @Test
     void whenSwitchRole_thenUserIsAlreadyAdmin() {

        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        DomainException exception =
                assertThrows(DomainException.class, () -> userService.switchRole(userId));

        assertEquals("This user is already an admin!", exception.getMessage());
    }


    @Test
     void whenSwitchRole_thenSuccess() {

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
     void whenRegister_thenUserAlreadyExist() {
        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("user");
        dto.setEmail("existing_user@example.com");
        dto.setPassword("123123");

        User excitingUser = User.builder()
                .username("user")
                .email("existing_user@example.com")
                .password("123123")
                .build();

        when(userRepository.findByUsernameOrEmail("user", "existing_user@example.com"))
                .thenReturn(Optional.of(excitingUser));

        UsernameAlreadyExistException exception = assertThrows(UsernameAlreadyExistException.class, ()
        -> userService.register(dto));

        assertEquals("This username or email already exist.", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(walletService, never()).createWallet(any());
        verify(emailService, never()).savePreference(any(UUID.class), anyBoolean(), anyString());
    }


    @Test
    void happyPath_whenRegister_thenUserRegisterAndSaveDatabase_withRoleAdmin() {

        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("user");
        dto.setFirstName("User");
        dto.setPassword("123123");
        dto.setConfirmPassword("123123");
        dto.setEmail("user@abv.bg");

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .password(dto.getPassword())
                .build();

        lenient().when(userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail())).thenReturn(Optional.empty());
        lenient().when(userRepository.save(any())).thenReturn(user);
        lenient().when(userRepository.count()).thenReturn(0L);

        userService.register(dto);

        verify(userRepository, times(1)).save(userCaptor.capture());

        User registeredUser = userCaptor.getValue();

        assertEquals("user", registeredUser.getUsername());
        assertEquals("User", registeredUser.getFirstName());
        assertEquals("user@abv.bg", registeredUser.getEmail());


        assertEquals(UserRole.ADMIN, registeredUser.getRole());


        verify(emailService, times(1))
                .savePreference(registeredUser.getId(), true, registeredUser.getEmail());
        verify(walletService, times(1))
                .createWallet(registeredUser);
    }


    @Test
    void happyPath_whenRegister_thenUserRegisterAndSaveDatabase_withRoleUser() {

        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("user");
        dto.setFirstName("User");
        dto.setPassword("123123");
        dto.setConfirmPassword("123123");
        dto.setEmail("user@abv.bg");

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .password(dto.getPassword())
                .build();


        lenient().when(userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail())).thenReturn(Optional.empty());
        lenient().when(userRepository.count()).thenReturn(1L);
        lenient().when(userRepository.save(any())).thenReturn(user);

        userService.register(dto);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User registeredUser = userCaptor.getValue();

        assertEquals("user", registeredUser.getUsername());
        assertEquals("User", registeredUser.getFirstName());
        assertEquals("user@abv.bg", registeredUser.getEmail());

        assertEquals(UserRole.USER, registeredUser.getRole());

        verify(emailService, times(1)).savePreference(registeredUser.getId(), true, registeredUser.getEmail());
        verify(walletService, times(1)).createWallet(registeredUser);
    }


        @Test
    void whenEditUserData_thenChangeDetailsAndSaveDatabase() {

        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder()
                .firstName("Mimi")
                .profilePicture("www.image.com")
                .email("mimi@abv.bg")
                .username("mimi123")
                .build();

        User user = User.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserData(userId, dto);

        assertEquals("Mimi", user.getFirstName());
        assertEquals("www.image.com", user.getProfilePicture());
        assertEquals("mimi@abv.bg", user.getEmail());
        assertEquals("mimi123", user.getUsername());
    }


    @Test
    void whenLoadUserByUsername_thenUsernameNotExistException() {

        String username = "user";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username));

        assertEquals("Username does not exist!", exception.getMessage());
    }


    @Test
    void whenLoadUserByUsername_thenReturnCorrectAuthenticationDetails() {

        String username = "Ani123";

        User user = User.builder()
                .id(UUID.randomUUID())
                .password("123123")
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails authenticationDetails = userService.loadUserByUsername(username);

        assertTrue(authenticationDetails instanceof AuthenticationDetails);
        AuthenticationDetails result = (AuthenticationDetails) authenticationDetails;

        assertEquals(user.getId(), result.getUserId());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
    }


    @Test
    void whenGetAllUsers_thenOnlyUsersWithRoleUserReturned() {

        User user1 = User.builder().username("user1").role(UserRole.USER).build();
        User user2 = User.builder().username("user2").role(UserRole.ADMIN).build();
        User user3 = User.builder().username("user3").role(UserRole.USER).build();
        User user4 = User.builder().username("user4").role(UserRole.ADMIN).build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2, user3, user4));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user3));
        assertFalse(result.contains(user2));
        assertFalse(result.contains(user4));
    }

    @Test
    void whenGetById_thenIdDoesNotExist() {

        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DomainException exception =
                assertThrows(DomainException.class, () -> userService.getById(userId));

        assertEquals("User with id [%s] does not exist.".formatted(userId),
                exception.getMessage());
    }
}





