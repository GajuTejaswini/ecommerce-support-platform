package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.CreateOrderRequest;
import com.ecommerce.orderservice.dto.OrderStatusUpdateRequest;
import com.ecommerce.orderservice.dto.TrackingInfo;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.UserRole;
import com.ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader(value = "Authorization") String authHeader,
            @Valid @RequestBody CreateOrderRequest request) {
        try {
            logger.info("Received order creation request: {}", request);
            
            // Extract user ID from token or use a default for testing
            Long userId = 1L;
            
            Order order = new Order();
            order.setUserId(userId);
            order.setStatus("PROCESSING");
            order.setOrderDate(LocalDateTime.now());
            order.setShippingAddress(request.getShippingAddress());
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(3));
            order.setCurrentLocation("Warehouse - Processing Center");
            order.setTrackingNumber(generateTrackingNumber());
            order.setCourierName("EcomExpress");
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(request.getProductId());
            orderItem.setProductName(request.getProductName());
            orderItem.setPrice(request.getPrice());
            orderItem.setQuantity(request.getQuantity());
            
            order.addOrderItem(orderItem);
            order.setTotalAmount(request.getPrice() * request.getQuantity());
            
            logger.info("Processing order with details: {}", order);
            
            Order createdOrder = orderService.createOrder(order);
            logger.info("Order created successfully with ID: {}", createdOrder.getId());
            
            return ResponseEntity.ok(createdOrder);
            
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestHeader(value = "Authorization") String authHeader) {
        try {
            // Extract user ID from token or use a default for testing
            Long userId = 1L;
            logger.info("Fetching orders for user ID: {}", userId);
            
            List<Order> orders = orderService.getOrdersByUserId(userId);
            logger.info("Found {} orders for user ID: {}", orders.size(), userId);
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/track/{orderId}")
    public ResponseEntity<?> trackMyOrder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Long orderId) {
        try {
            // Extract user ID from token or use a default for testing
            Long userId = 1L;
            return ResponseEntity.ok(orderService.trackOrder(userId, orderId));
        } catch (Exception e) {
            logger.error("Error tracking order: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllOrders(@RequestHeader(value = "Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            logger.error("Error fetching all orders: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/admin/track/{orderId}")
    public ResponseEntity<?> trackAnyOrder(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(orderService.trackOrder(orderId));
        } catch (Exception e) {
            logger.error("Error tracking order: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request,
            BindingResult bindingResult) {
        try {
            logger.info("Received order status update request for orderId: {} with data: {}", orderId, request);

            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                logger.error("Validation errors: {}", errors);
                return ResponseEntity.badRequest().body(createValidationErrorResponse(errors, orderId));
            }

            // Validate order exists
            Order existingOrder = orderService.findById(orderId);
            if (existingOrder == null) {
                logger.error("Order not found with ID: {}", orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Order not found with ID: " + orderId, orderId));
            }

            // Validate status
            if (!isValidStatus(request.getStatus())) {
                String errorMessage = "Invalid status. Allowed values are: " + 
                    String.join(", ", Arrays.asList("PENDING", "PROCESSING", "SHIPPED", "IN_TRANSIT", "DELIVERED", "CANCELLED"));
                logger.error("Invalid status provided: {}", request.getStatus());
                return ResponseEntity.badRequest()
                    .body(createErrorResponse(errorMessage, orderId));
            }

            // Update order
            Order updatedOrder = orderService.updateOrderStatus(
                orderId, 
                request.getStatus().toUpperCase(), 
                request.getLocation(), 
                request.getEstimatedDeliveryDate()
            );

            logger.info("Successfully updated order status. OrderId: {}, New Status: {}", orderId, request.getStatus());
            return ResponseEntity.ok(updatedOrder);

        } catch (Exception e) {
            logger.error("Error updating order status for orderId: " + orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Error updating order: " + e.getMessage(), orderId));
        }
    }

    @GetMapping("/admin/track/all")
    public ResponseEntity<?> trackAllOrders(
            @RequestHeader(value = "Authorization") String authHeader) {
        try {
            logger.info("Admin requesting to track all orders");
            List<Order> allOrders = orderService.getAllOrders();
            List<TrackingInfo> allTrackingInfo = allOrders.stream()
                .map(order -> {
                    TrackingInfo trackingInfo = new TrackingInfo();
                    trackingInfo.setOrderId(order.getId());
                    trackingInfo.setStatus(order.getStatus());
                    trackingInfo.setCurrentLocation(order.getCurrentLocation());
                    trackingInfo.setTrackingNumber(order.getTrackingNumber());
                    trackingInfo.setCourierName(order.getCourierName());
                    trackingInfo.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
                    return trackingInfo;
                })
                .toList();
            
            logger.info("Found {} orders to track", allTrackingInfo.size());
            return ResponseEntity.ok(allTrackingInfo);
        } catch (Exception e) {
            logger.error("Error tracking all orders: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/admin/track/status/{status}")
    public ResponseEntity<?> trackOrdersByStatus(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable String status) {
        try {
            logger.info("Admin requesting to track orders with status: {}", status);
            if (!isValidStatus(status)) {
                String errorMessage = "Invalid status. Allowed values are: " + 
                    String.join(", ", Arrays.asList("PENDING", "PROCESSING", "SHIPPED", "IN_TRANSIT", "DELIVERED", "CANCELLED"));
                return ResponseEntity.badRequest().body(createErrorResponse(errorMessage, null));
            }

            List<Order> orders = orderService.getOrdersByStatus(status.toUpperCase());
            List<TrackingInfo> trackingInfoList = orders.stream()
                .map(order -> {
                    TrackingInfo trackingInfo = new TrackingInfo();
                    trackingInfo.setOrderId(order.getId());
                    trackingInfo.setStatus(order.getStatus());
                    trackingInfo.setCurrentLocation(order.getCurrentLocation());
                    trackingInfo.setTrackingNumber(order.getTrackingNumber());
                    trackingInfo.setCourierName(order.getCourierName());
                    trackingInfo.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
                    return trackingInfo;
                })
                .toList();
            
            logger.info("Found {} orders with status {}", trackingInfoList.size(), status);
            return ResponseEntity.ok(trackingInfoList);
        } catch (Exception e) {
            logger.error("Error tracking orders by status: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidStatus(String status) {
        if (status == null) return false;
        return Arrays.asList(
            "PENDING", "PROCESSING", "SHIPPED", 
            "IN_TRANSIT", "DELIVERED", "CANCELLED"
        ).contains(status.toUpperCase());
    }

    private Map<String, Object> createErrorResponse(String message, Long orderId) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", message);
        response.put("path", "/api/orders/admin/" + orderId + "/status");
        return response;
    }

    private Map<String, Object> createValidationErrorResponse(Map<String, String> errors, Long orderId) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("path", "/api/orders/admin/" + orderId + "/status");
        return response;
    }

    private String generateTrackingNumber() {
        return "EC" + System.currentTimeMillis();
    }
} 