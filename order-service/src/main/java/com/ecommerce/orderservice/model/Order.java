package com.ecommerce.orderservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String status;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private LocalDateTime estimatedDeliveryDate;
    private String currentLocation;
    private String trackingNumber;
    private String courierName;
    private String estimatedDeliveryLocation;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
} 