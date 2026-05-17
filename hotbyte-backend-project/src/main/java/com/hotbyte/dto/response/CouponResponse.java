package com.hotbyte.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {

    private Long id;
    private String code;
    private String title;
    private String description;
    private String discountType;        // "PERCENTAGE" or "FLAT"
    private Double discountValue;
    private Double minOrderAmount;
    private Double maxDiscount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private boolean isExpired;
    private String offerType;           // "COUPON", "ITEM_DISCOUNT", "RESTAURANT_DEAL"
    private Long restaurantId;
    private String restaurantName;
    private Long menuItemId;
    private String menuItemName;
    private String badgeColor;
    private String imageUrl;
    private String savingsText;         // e.g. "Save up to ₹100"
    private Long daysRemaining;
}