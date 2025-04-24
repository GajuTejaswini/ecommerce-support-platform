package com.ecommerce.ticketservice.dto;

import com.ecommerce.ticketservice.model.TicketCategory;
import com.ecommerce.ticketservice.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {
    private Long orderId;  // Will be set from path variable

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private TicketCategory category;

    @NotNull(message = "Priority is required")
    private TicketPriority priority;

    @NotNull(message = "User ID is required")
    private Long userId;
} 