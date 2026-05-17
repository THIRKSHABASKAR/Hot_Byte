// REPLACE your existing Coupon.java with this
package com.hotbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // "PERCENTAGE" or "FLAT"
    @Column(nullable = false)
    private String discountType;

    @Column(nullable = false)
    private Double discountValue;

    private Double minOrderAmount;

    private Double maxDiscount;  // cap for percentage discounts

    private Integer usageLimit;  // null = unlimited

    @Builder.Default
    private Integer usedCount = 0;

    private LocalDateTime expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    // "COUPON", "ITEM_DISCOUNT", "RESTAURANT_DEAL"
    @Column(nullable = false)
    private String offerType;

    // null = applies to all restaurants
    private Long restaurantId;

    // for ITEM_DISCOUNT type
    private Long menuItemId;

    // "orange", "green", "blue", "purple"
    @Builder.Default
    private String badgeColor = "orange";

    private String imageUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}