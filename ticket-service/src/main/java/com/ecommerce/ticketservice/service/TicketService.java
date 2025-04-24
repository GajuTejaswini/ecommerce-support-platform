package com.ecommerce.ticketservice.service;

import com.ecommerce.ticketservice.dto.AssignTicketRequest;
import com.ecommerce.ticketservice.dto.TicketRequest;
import com.ecommerce.ticketservice.dto.UpdateTicketRequest;
import com.ecommerce.ticketservice.model.Comment;
import com.ecommerce.ticketservice.model.Ticket;
import com.ecommerce.ticketservice.model.TicketStatus;
import com.ecommerce.ticketservice.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;

    public Ticket createTicket(TicketRequest request) {
        logger.info("Creating new ticket with subject: {}", request.getSubject());
        
        Ticket ticket = new Ticket();
        ticket.setOrderId(request.getOrderId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setUserId(request.getUserId());
        ticket.setStatus(TicketStatus.OPEN);
        
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByUserId(Long userId) {
        logger.info("Fetching tickets for user ID: {}", userId);
        return ticketRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketById(Long ticketId) {
        logger.info("Fetching ticket with ID: {}", ticketId);
        return ticketRepository.findById(ticketId)
            .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + ticketId));
    }

    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        logger.info("Fetching all tickets");
        return ticketRepository.findAll();
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, UpdateTicketRequest request) {
        logger.info("Updating ticket {} status to {}", ticketId, request.getStatus());
        
        Ticket ticket = getTicketById(ticketId);
        ticket.setStatus(request.getStatus());
        
        if (request.getResolution() != null) {
            ticket.setResolution(request.getResolution());
        }
        
        if (request.getStatus() == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        // Add comment if provided
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            Comment comment = new Comment();
            comment.setContent(request.getComment());
            comment.setAgentId(ticket.getAssignedAgentId());
            ticket.addComment(comment);
        }
        
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, AssignTicketRequest request) {
        logger.info("Assigning ticket {} to agent {}", ticketId, request.getAgentId());
        
        Ticket ticket = getTicketById(ticketId);
        ticket.setAssignedAgentId(request.getAgentId());
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        // Add automatic comment for assignment
        Comment comment = new Comment();
        comment.setContent("Ticket assigned to agent #" + request.getAgentId());
        comment.setAgentId(request.getAgentId());
        ticket.addComment(comment);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getUnassignedTickets() {
        logger.info("Fetching unassigned tickets");
        return ticketRepository.findByAssignedAgentIdIsNull();
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByAgentId(Long agentId) {
        logger.info("Fetching tickets assigned to agent: {}", agentId);
        return ticketRepository.findByAssignedAgentId(agentId);
    }

    public List<Ticket> getTicketsByOrderId(Long orderId) {
        return ticketRepository.findByOrderId(orderId);
    }

    public List<Ticket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status);
    }
} 