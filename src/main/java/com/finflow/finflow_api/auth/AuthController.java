package com.finflow.finflow_api.auth;

import com.finflow.finflow_api.common.dto.ApiResponse;
import com.finflow.finflow_api.user.dto.LoginRequest;
import com.finflow.finflow_api.user.dto.LoginResponse;
import com.finflow.finflow_api.user.dto.RegisterRequest;
import com.finflow.finflow_api.user.dto.UserResponse;
import com.finflow.finflow_api.user.dto.ChangePasswordRequest;
import com.finflow.finflow_api.user.entity.User;
import com.finflow.finflow_api.user.service.UserServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private  final UserServiceInterface userServiceInterface;

    public AuthController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginRequest(@Valid @RequestBody LoginRequest loginRequest){

        String accessToken = userServiceInterface.loginRequest(loginRequest);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setTokenType("Bearer");
        ApiResponse<LoginResponse> apiResponse=new ApiResponse<>(
                true,
                "Logged in successfully",
                loginResponse
        );
        return ResponseEntity.ok(apiResponse);

    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> userRegistration(@Valid @RequestBody RegisterRequest registerRequest){

           User user= userServiceInterface.userRegistration(registerRequest);
           UserResponse userResponse = new UserResponse(user.getId(),user.getName(),user.getEmail());
            ApiResponse<UserResponse> apiResponse=new ApiResponse<>(
                    true,
                    "user registered successfully",
                    userResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
           }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        userServiceInterface.changePassword(userId, request);
        ApiResponse<?> response = new ApiResponse<>(
                true,
                "Password changed successfully",
                null
        );
        return ResponseEntity.ok(response);
    }
}
