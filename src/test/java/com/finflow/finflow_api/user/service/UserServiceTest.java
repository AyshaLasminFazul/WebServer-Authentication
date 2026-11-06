
package com.finflow.finflow_api.user.service;

import com.finflow.finflow_api.auth.JwtService;
import com.finflow.finflow_api.common.exception.*;
import com.finflow.finflow_api.user.dto.ChangePasswordRequest;
import com.finflow.finflow_api.user.dto.LoginRequest;
import com.finflow.finflow_api.user.dto.RegisterRequest;
import com.finflow.finflow_api.user.entity.User;
import com.finflow.finflow_api.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void userRegistration_ShouldRegisterSuccessfully() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@gmail.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User savedUser = userService.userRegistration(request);

        // Assert
        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPasswordHash());
        assertEquals("CUSTOMER", savedUser.getRole());
        assertEquals("ACTIVE", savedUser.getStatus());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123");
    }

    @Test
    void userRegistration_ShouldThrowException_WhenEmailAlreadyExists() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@gmail.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateEmailException.class,
                () -> userService.userRegistration(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }


    @Test
    void loginRequest_ShouldReturnJwtToken_WhenCredentialsAreValid() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("Password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPasswordHash("hashedPassword");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "hashedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("mock-jwt-token");

        // Act
        String token = userService.loginRequest(request);

        // Assert
        assertNotNull(token);
        assertEquals("mock-jwt-token", token);

        verify(jwtService).generateToken(user);
    }

    @Test
    void loginRequest_ShouldThrowException_WhenPasswordIsIncorrect() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("WrongPassword");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPasswordHash("hashedPassword");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("WrongPassword", "hashedPassword"))
                .thenReturn(false);

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginRequest(request)
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void loginRequest_ShouldThrowException_WhenEmailDoesNotExist() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@gmail.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginRequest(request)
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenRequestIsValid() {

        // Arrange
        Long userId = 1L;

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");

        User user = new User();
        user.setId(userId);
        user.setPasswordHash("oldHashedPassword");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("OldPassword123", "oldHashedPassword"))
                .thenReturn(true);

        when(passwordEncoder.matches("NewPassword123", "oldHashedPassword"))
                .thenReturn(false);

        when(passwordEncoder.encode("NewPassword123"))
                .thenReturn("newHashedPassword");

        // Act
        userService.changePassword(userId, request);

        // Assert
        assertEquals("newHashedPassword", user.getPasswordHash());

        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenCurrentPasswordIsIncorrect() {

        // Arrange
        Long userId = 1L;

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("WrongPassword");
        request.setNewPassword("NewPassword123");

        User user = new User();
        user.setId(userId);
        user.setPasswordHash("oldHashedPassword");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("WrongPassword", "oldHashedPassword"))
                .thenReturn(false);

        // Act & Assert
        assertThrows(
                InvalidCurrentPasswordException.class,
                () -> userService.changePassword(userId, request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordIsSameAsOldPassword() {

        // Arrange
        Long userId = 1L;

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("OldPassword123");

        User user = new User();
        user.setId(userId);
        user.setPasswordHash("oldHashedPassword");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                PasswordChangeException.class,
                () -> userService.changePassword(userId, request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ShouldThrowException_WhenUserDoesNotExist() {

        // Arrange
        Long userId = 100L;

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.changePassword(userId, request)
        );

        verify(userRepository, never()).save(any(User.class));
    }


}
