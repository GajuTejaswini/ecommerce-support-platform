package com.ecommerce.ticketservice.dto;

import com.ecommerce.ticketservice.model.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketRequest {
    @NotNull(message = "Status is required")
    private TicketStatus status;

    private String resolution;  // Required when status is RESOLVED or CLOSED

    private String comment;  // Optional comment for the update
} 