package com.ecommerce.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderStatusUpdateRequest {
    @NotBlank(message = "Status cannot be empty")
    private String status;
    
    private String location;
    
    private LocalDateTime estimatedDeliveryDate;
    
    // Note: If you need estimatedDeliveryLocation, uncomment the line below
    // private String estimatedDeliveryLocation;
} 