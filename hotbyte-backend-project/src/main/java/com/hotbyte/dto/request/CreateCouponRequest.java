package com.hotbyte.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateCouponRequest {

    private String code;           // e.g. "SAVE20"
    private String title;          // e.g. "20% off your first order"
    private String description;    // longer description
    private String discountType;   // "PERCENTAGE" or "FLAT"
    private Double discountValue;  // 20 for 20% or 50 for ₹50 off
    private Double minOrderAmount; // minimum cart value to apply
    private Double maxDiscount;    // cap for percentage discounts
    private Integer usageLimit;    // total usage limit (null = unlimited)
    private LocalDateTime expiryDate;
    private String offerType;      // "COUPON", "ITEM_DISCOUNT", "RESTAURANT_DEAL"
    private Long restaurantId;     // null means applies to all restaurants
    private Long menuItemId;       // for ITEM_DISCOUNT type
    private String badgeColor;     // "orange", "green", "blue", "purple"
    private String imageUrl;       // optional banner image
}