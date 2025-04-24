package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.TrackingInfo;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        logger.info("Creating new order for user ID: {}", order.getUserId());
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }
        
        order.getOrderItems().forEach(item -> item.setOrder(order));
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(Long userId) {
        logger.info("Fetching orders for user ID: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        logger.info("Found {} orders for user ID: {}", orders.size(), userId);
        return orders;
    }

    @Transactional(readOnly = true)
    public TrackingInfo trackOrder(Long userId, Long orderId) {
        logger.info("Tracking order ID: {} for user ID: {}", orderId, userId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found or not authorized"));
        
        return buildTrackingInfo(order);
    }

    @Transactional(readOnly = true)
    public TrackingInfo trackOrder(Long orderId) {
        logger.info("Admin tracking order ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        return buildTrackingInfo(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        logger.info("Found {} total orders", orders.size());
        return orders;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String status, String location, LocalDateTime estimatedDeliveryDate) {
        logger.info("Updating status for order ID: {} to {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        order.setStatus(status);
        if (location != null) {
            order.setCurrentLocation(location);
        }
        if (estimatedDeliveryDate != null) {
            order.setEstimatedDeliveryDate(estimatedDeliveryDate);
        }
        
        Order updatedOrder = orderRepository.save(order);
        logger.info("Order status updated successfully for ID: {}", orderId);
        return updatedOrder;
    }

    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(String status) {
        logger.info("Fetching orders with status: {}", status);
        List<Order> orders = orderRepository.findByStatus(status);
        logger.info("Found {} orders with status {}", orders.size(), status);
        return orders;
    }

    private TrackingInfo buildTrackingInfo(Order order) {
        TrackingInfo trackingInfo = new TrackingInfo();
        trackingInfo.setOrderId(order.getId());
        trackingInfo.setStatus(order.getStatus());
        trackingInfo.setCurrentLocation(order.getCurrentLocation());
        trackingInfo.setTrackingNumber(order.getTrackingNumber());
        trackingInfo.setCourierName(order.getCourierName());
        trackingInfo.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        return trackingInfo;
    }
} 