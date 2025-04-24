package com.ecommerce.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingInfo {
    private Long orderId;
    private String status;
    private String currentLocation;
    private LocalDateTime estimatedDeliveryDate;
    private String trackingNumber;
    private String courierName;
} 