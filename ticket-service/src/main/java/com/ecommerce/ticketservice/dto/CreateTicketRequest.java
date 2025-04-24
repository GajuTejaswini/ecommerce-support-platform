package com.ecommerce.ticketservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTicketRequest {
    @NotBlank(message = "Subject is required")
    @Size(min = 5, max = 100, message = "Subject must be between 5 and 100 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    private String category;
} 