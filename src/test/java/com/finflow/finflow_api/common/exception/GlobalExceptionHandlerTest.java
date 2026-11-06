
package com.finflow.finflow_api.common.exception;

import com.finflow.finflow_api.common.logging.GlobalErrorNotifier;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private GlobalErrorNotifier globalErrorNotifier;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {

        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<?> response =
                globalExceptionHandler.handleGenericException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq("Unexpected application error"), same(exception), same(request));
    }

    @Test
    void handleDuplicateEmail_ShouldReturnBadRequest() {

        DuplicateEmailException exception =
                new DuplicateEmailException("Email already exists");

        ResponseEntity<?> response =
                globalExceptionHandler.handleDuplicateEmail(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq("User registration failed"), same(exception), same(request));
    }

    @Test
    void handleInvalidCredentials_ShouldReturnUnauthorized() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException("Invalid email or password");

        ResponseEntity<?> response =
                globalExceptionHandler.handleInvalidCredentials(exception, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq("User login failed"), same(exception), same(request));
    }

    @Test
    void handleInvalidCurrentPassword_ShouldReturnBadRequest() {

        InvalidCurrentPasswordException exception =
                new InvalidCurrentPasswordException("Current password is incorrect");

        ResponseEntity<?> response =
                globalExceptionHandler.handleInvalidCurrentPassword(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq(exception.getMessage()), same(exception), same(request));
    }

    @Test
    void handlePasswordChange_ShouldReturnBadRequest() {

        PasswordChangeException exception =
                new PasswordChangeException("New password must be different");

        ResponseEntity<?> response =
                globalExceptionHandler.handlePasswordChange(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq(exception.getMessage()), same(exception), same(request));
    }

    @Test
    void handleResourceNotFound_ShouldReturnBadRequest() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException("User not found");

        ResponseEntity<?> response =
                globalExceptionHandler.handleResourceNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND   , response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq(exception.getMessage()), same(exception), same(request));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {

        // Arrange
        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError =
                new FieldError("registerRequest", "email", "must not be blank");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<?> response =
                globalExceptionHandler.handleValidationException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(globalErrorNotifier).notifyError(
                eq("Request validation failed"), same(exception), same(request));
    }
}
