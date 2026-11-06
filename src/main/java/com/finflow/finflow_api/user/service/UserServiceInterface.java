package com.finflow.finflow_api.user.service;

import com.finflow.finflow_api.user.dto.ChangePasswordRequest;
import com.finflow.finflow_api.user.dto.LoginRequest;
import com.finflow.finflow_api.user.dto.RegisterRequest;
import com.finflow.finflow_api.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserServiceInterface {

    boolean existsByEmailId(String Email);

    User userRegistration(RegisterRequest registerRequest);

    String loginRequest(LoginRequest loginRequest);

    void changePassword(Long userId, ChangePasswordRequest request);
}
