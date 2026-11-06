package com.finflow.finflow_api.common.exception;

import com.finflow.finflow_api.common.dto.ApiResponse;
import com.finflow.finflow_api.common.logging.GlobalErrorNotifier;
import com.finflow.finflow_api.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final GlobalErrorNotifier globalErrorNotifier;

    public GlobalExceptionHandler(GlobalErrorNotifier globalErrorNotifier) {
        this.globalErrorNotifier = globalErrorNotifier;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception exception, HttpServletRequest request) {

        globalErrorNotifier.notifyError("Unexpected application error", exception, request);
        ApiResponse<?> response = new ApiResponse<>(
                false,
                "Something went wrong. Please try again later.",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<?>>  handleDuplicateEmail(DuplicateEmailException exception, HttpServletRequest request){

        globalErrorNotifier.notifyError("User registration failed", exception, request);
        ApiResponse<UserResponse> apiResponse= new ApiResponse<>(
                false,
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ApiResponse<?>>  handleInvalidCurrentPassword(InvalidCurrentPasswordException exception, HttpServletRequest request){

        globalErrorNotifier.notifyError(exception.getMessage(), exception, request);
        ApiResponse<UserResponse> apiResponse= new ApiResponse<>(
                false,
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
    @ExceptionHandler(PasswordChangeException.class)
    public ResponseEntity<ApiResponse<?>>  handlePasswordChange(PasswordChangeException exception, HttpServletRequest request){

        globalErrorNotifier.notifyError(exception.getMessage(), exception, request);
        ApiResponse<UserResponse> apiResponse= new ApiResponse<>(
                false,
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>>  handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request){

        globalErrorNotifier.notifyError(exception.getMessage(), exception, request);
        ApiResponse<UserResponse> apiResponse= new ApiResponse<>(
                false,
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidCredentials(InvalidCredentialsException exception,HttpServletRequest request){

        globalErrorNotifier.notifyError("User login failed", exception, request);
        ApiResponse<UserResponse> apiResponse=new ApiResponse<>(false, exception.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {

        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        globalErrorNotifier.notifyError("Request validation failed", exception, request);

        ApiResponse<?> response = new ApiResponse<>(false, errorMessage, null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
