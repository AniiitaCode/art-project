package com.example.art.user.service;

import com.example.art.email.service.EmailService;
import com.example.art.exception.DomainException;
import com.example.art.exception.UsernameAlreadyExistException;
import com.example.art.security.AuthenticationDetails;
import com.example.art.user.model.User;
import com.example.art.user.model.UserRole;
import com.example.art.user.repository.UserRepository;
import com.example.art.wallet.service.WalletService;
import com.example.art.web.dto.RegisterRequest;
import com.example.art.web.dto.UserEditRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final WalletService walletService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.walletService = walletService;
    }

 //   @Transactional
    public void register(RegisterRequest registerRequest) {
        Optional<User> optionalUser =
                userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistException("This username or email already exist.");
        }

            User user = User.builder()
                    .firstName(registerRequest.getFirstName())
                    .email(registerRequest.getEmail())
                    .createdOn(LocalDateTime.now())
                    .username(registerRequest.getUsername())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .build();

        if (userRepository.count() == 0) {
            user.setRole(UserRole.ADMIN);
        }  else  {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
        walletService.createWallet(user);
        emailService.savePreference(user.getId(), true, user.getEmail());
        emailService.sendEmail(user.getId(),
                "Welcome", ("Dear %s, welcome to our website! " +
                        "Congratulations! As a new user, you receive a gift of 20 BGN!")
                .formatted(user.getFirstName()));
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("User with id [%s] does not exist."
                        .formatted(userId)));
    }

    public void editUserData(UUID id,
                             UserEditRequest userEditRequest) {

        User user = getById(id);
        user.setFirstName(userEditRequest.getFirstName());
        user.setEmail(userEditRequest.getEmail());
        user.setUsername(userEditRequest.getUsername());
        user.setProfilePicture(userEditRequest.getProfilePicture());
        user.setUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
    }

    public void switchRole(UUID id) {
        User userToUpdate = getById(id);

        if (userToUpdate == null) {
            throw new DomainException("User with id [%s] does not exist.".formatted(id));
        }

        if (userToUpdate.getRole() == UserRole.ADMIN) {
            throw new DomainException("This user is already an admin!");
        }

        userToUpdate.setRole(UserRole.ADMIN);

        userRepository.save(userToUpdate);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username does not exist!"));

        return new AuthenticationDetails(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().equals(UserRole.USER))
                .collect(Collectors.toList());
    }
}
