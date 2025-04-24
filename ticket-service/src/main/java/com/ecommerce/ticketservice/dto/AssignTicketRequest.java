package com.ecommerce.ticketservice.dto;

import com.ecommerce.ticketservice.model.TicketPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTicketRequest {
    @NotNull(message = "Agent ID is required")
    private Long agentId;
    
    private TicketPriority priority;
} 