package com.ecommerce.ticketservice.model;

public enum TicketStatus {
    OPEN,           // Newly created ticket
    IN_PROGRESS,    // Agent is working on it
    RESOLVED,       // Issue has been resolved
    CLOSED          // Ticket is closed
} 