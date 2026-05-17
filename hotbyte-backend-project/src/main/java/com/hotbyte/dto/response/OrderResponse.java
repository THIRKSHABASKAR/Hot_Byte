package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private String restaurantName;
    private String restaurantImage;
    private List<OrderItemResponse> items;
    private String deliveryAddress;
    private String deliveryPhone;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal couponDiscount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String status;
    private LocalDateTime placedAt;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveredAt;
}