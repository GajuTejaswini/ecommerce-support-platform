package com.ecommerce.ticketservice.repository;

import com.ecommerce.ticketservice.model.Ticket;
import com.ecommerce.ticketservice.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByOrderId(Long orderId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByAssignedAgentId(Long agentId);
    List<Ticket> findByAssignedAgentIdIsNull();
    List<Ticket> findAllByOrderByCreatedAtDesc();
} 