package com.fixly.fixlybackend.service;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fixly.fixlybackend.model.User;
import com.fixly.fixlybackend.model.UserRole;
import com.fixly.fixlybackend.repository.BookingRepository;
import com.fixly.fixlybackend.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldSetCustomerRole() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@fixly.com");
        user.setPhone("07123456789");
        user.setPassword("Password123");

        when(userRepository.existsByEmail("test@fixly.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.createUser(user);

        assertEquals(UserRole.CUSTOMER, savedUser.getRole());
    }

    @Test
    void createUserShouldEncodePassword() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@fixly.com");
        user.setPhone("07123456789");
        user.setPassword("Password123");

        when(userRepository.existsByEmail("test@fixly.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.createUser(user);

        assertEquals("encodedPassword", savedUser.getPassword());
    }
    @Test
    void createUserShouldRejectDuplicateEmail() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("existing@fixly.com");
        user.setPhone("07123456789");
        user.setPassword("Password123");

        when(userRepository.existsByEmail("existing@fixly.com"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(user)
                );

        assertEquals(
                "Email already exists",
                exception.getMessage()
        );
    }

    @Test
    void createUserShouldRejectShortPassword() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@fixly.com");
        user.setPhone("07123456789");
        user.setPassword("12345");

        when(userRepository.existsByEmail("test@fixly.com"))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(user)
                );

        assertEquals(
                "Password must be at least 8 characters",
                exception.getMessage()
        );
    }

    @Test
    void loginShouldRejectWrongPassword() {

        User user = new User();
        user.setEmail("test@fixly.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@fixly.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.login(
                                "test@fixly.com",
                                "WrongPassword"
                        )
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }
    @Test
    void loginShouldReturnUserWhenCredentialsAreCorrect() {

        User user = new User();
        user.setEmail("test@fixly.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@fixly.com"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches(
                "Password123",
                "encodedPassword"
        )).thenReturn(true);

        User loggedInUser = userService.login(
                "test@fixly.com",
                "Password123"
        );

        assertEquals(user, loggedInUser);
    }
}
