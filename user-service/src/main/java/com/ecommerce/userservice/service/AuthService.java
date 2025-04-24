package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, 
                      JwtTokenProvider tokenProvider,
                      UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // Find user by email first
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Authenticate using username (email) and password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getUsername(), // Use username for authentication
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new LoginResponse(jwt, user.getEmail(), user.getRole().name());
    }
} 