package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    // If no password is provided, it means the user doesn't want to update their password
    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }
} 