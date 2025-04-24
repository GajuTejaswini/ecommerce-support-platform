package com.ecommerce.ticketservice.controller;

import com.ecommerce.ticketservice.dto.AssignTicketRequest;
import com.ecommerce.ticketservice.dto.TicketRequest;
import com.ecommerce.ticketservice.dto.UpdateTicketRequest;
import com.ecommerce.ticketservice.model.Ticket;
import com.ecommerce.ticketservice.model.TicketStatus;
import com.ecommerce.ticketservice.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TicketController {
    private final TicketService ticketService;

    // Customer Endpoints
    @PostMapping("/order/{orderId}")
    public ResponseEntity<Ticket> createOrderTicket(
            @PathVariable Long orderId,
            @Valid @RequestBody TicketRequest request) {
        log.info("Creating ticket for order: {}", orderId);
        request.setOrderId(orderId);
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getUserTickets(@PathVariable Long userId) {
        log.info("Fetching tickets for user: {}", userId);
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Ticket>> getOrderTickets(@PathVariable Long orderId) {
        log.info("Fetching tickets for order: {}", orderId);
        return ResponseEntity.ok(ticketService.getTicketsByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable TicketStatus status) {
        log.info("Fetching tickets by status: {}", status);
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long ticketId) {
        log.info("Fetching ticket: {}", ticketId);
        return ResponseEntity.ok(ticketService.getTicketById(ticketId));
    }

    // Admin Endpoints
    @GetMapping("/admin/all")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        log.info("Fetching all tickets");
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/admin/unassigned")
    public ResponseEntity<List<Ticket>> getUnassignedTickets() {
        log.info("Fetching unassigned tickets");
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }

    @PutMapping("/admin/{ticketId}/assign")
    public ResponseEntity<Ticket> assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AssignTicketRequest request) {
        log.info("Assigning ticket {} to agent {}", ticketId, request.getAgentId());
        return ResponseEntity.ok(ticketService.assignTicket(ticketId, request));
    }

    // Agent Endpoints
    @GetMapping("/agent/{agentId}/assigned")
    public ResponseEntity<List<Ticket>> getAgentTickets(@PathVariable Long agentId) {
        log.info("Fetching tickets assigned to agent: {}", agentId);
        return ResponseEntity.ok(ticketService.getTicketsByAgentId(agentId));
    }

    @PutMapping("/agent/{ticketId}/update")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketRequest request) {
        log.info("Updating ticket {} status to {}", ticketId, request.getStatus());
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, request));
    }
} 