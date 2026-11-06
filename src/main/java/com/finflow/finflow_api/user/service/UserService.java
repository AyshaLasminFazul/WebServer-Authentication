package com.finflow.finflow_api.user.service;

import com.finflow.finflow_api.auth.JwtService;
import com.finflow.finflow_api.common.exception.*;
import com.finflow.finflow_api.user.dto.ChangePasswordRequest;
import com.finflow.finflow_api.user.dto.LoginRequest;
import com.finflow.finflow_api.user.dto.RegisterRequest;
import com.finflow.finflow_api.user.entity.User;
import com.finflow.finflow_api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UserService implements UserServiceInterface{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService)
    {
        this.userRepository=userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @Override
    public boolean existsByEmailId(String email) {

        return userRepository.existsByEmail(email);
    }

    @Override
    public User userRegistration(RegisterRequest registerRequest) {
        if(existsByEmailId(registerRequest.getEmail()))
            throw new DuplicateEmailException("Email already exists");
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user= new User();
        user.setName(registerRequest.getName());
        user.setPasswordHash(hashedPassword);
        user.setEmail(registerRequest.getEmail());
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);



    }

    @Override
    public String loginRequest(LoginRequest loginRequest) {
        Optional<User> optionalUser= userRepository.findByEmail(loginRequest.getEmail());
        if(optionalUser.isPresent()){
            User user=optionalUser.get();
         if( passwordEncoder.matches(loginRequest.getPassword(),
                  user.getPasswordHash()))  {

             String jwtToken = jwtService.generateToken(user);
            return jwtToken;
            }
         else
             throw new InvalidCredentialsException("Invalid email or password");
        }
        throw new InvalidCredentialsException("Invalid email or password");

    }
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        // Prevent reusing the current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new PasswordChangeException("New password must be different from current password");
        }

        // Hash the new password
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }
}
