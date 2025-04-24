package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.dto.UserUpdateRequest;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        // Ensure new users are assigned the USER role by default
        // unless specifically set as ADMIN or AGENT (which could be restricted to admin registration)
        if (registerRequest.getRole() == null) {
            user.setRole(Role.USER);
        } else {
            user.setRole(registerRequest.getRole());
        }
        
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = getUserById(id);
        
        if (userUpdateRequest.getUsername() != null) {
            // Check if new username is already taken by another user
            if (!user.getUsername().equals(userUpdateRequest.getUsername()) && 
                    userRepository.existsByUsername(userUpdateRequest.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userUpdateRequest.getUsername());
        }
        
        if (userUpdateRequest.getEmail() != null) {
            // Check if new email is already taken by another user
            if (!user.getEmail().equals(userUpdateRequest.getEmail()) && 
                    userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userUpdateRequest.getEmail());
        }
        
        if (userUpdateRequest.hasPassword()) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        
        return userRepository.save(user);
    }

    public User updateUserRole(Long id, Role role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
    
    public User updateCurrentUser(UserUpdateRequest userUpdateRequest) {
        User currentUser = getCurrentUser();
        return updateUser(currentUser.getId(), userUpdateRequest);
    }
} 