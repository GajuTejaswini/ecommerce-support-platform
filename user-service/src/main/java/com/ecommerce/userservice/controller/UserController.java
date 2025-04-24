package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.LoginRequest;
import com.ecommerce.userservice.dto.LoginResponse;
import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.dto.UserResponse;
import com.ecommerce.userservice.dto.UserUpdateRequest;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.service.AuthService;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(UserResponse.fromUser(userService.registerUser(registerRequest)), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        return ResponseEntity.ok(UserResponse.fromUser(userService.getCurrentUser()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(UserResponse.fromUser(userService.updateCurrentUser(userUpdateRequest)));
    }

    // Admin endpoints
    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.fromUser(userService.getUserById(id)));
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(UserResponse.fromUser(userService.updateUser(id, userUpdateRequest)));
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/admin/users/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(UserResponse.fromUser(userService.updateUserRole(id, role)));
    }

    // Agent endpoints
    @GetMapping("/agent/profile")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<UserResponse> getAgentProfile() {
        return ResponseEntity.ok(UserResponse.fromUser(userService.getCurrentUser()));
    }
} 