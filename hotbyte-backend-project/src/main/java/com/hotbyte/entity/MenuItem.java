package com.hotbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategory category;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String ingredients;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Availability availability = Availability.ALL_DAY;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_type", nullable = false)
    private FoodType foodType = FoodType.VEG;

    @Enumerated(EnumType.STRING)
    @Column(name = "taste_info")
    private TasteInfo tasteInfo;

    private Integer calories;
    private BigDecimal fats;
    private BigDecimal proteins;
    private BigDecimal carbohydrates;

    @Column(name = "cooking_time")
    private Integer cookingTime;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    
 // ADD these two fields to your existing MenuItem.java entity
 // (inside the class, alongside your existing fields)

     /**
      * Original price before discount.
      * If this is set and > price, the item appears in the Discounted Items section.
      * DealCard in OffersPage.js reads: item.originalPrice and item.price
      */
     @Column(name = "original_price")
     private Double originalPrice;

     /**
      * Optional per-item discount percentage label (e.g. 20 for "20% OFF").
      * Used by DealCard: item.discountPercent
      * If originalPrice is set, the frontend calculates it automatically.
      */
     @Column(name = "discount_percent")
     private Double discountPercent;

     // Also ensure these existing fields are present (add if missing):
     //   private String imageUrl;
     //   private Boolean isVeg;
     //   private String restaurantName;  ← or derive from restaurant relationship

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public enum Availability { ALL_DAY, BREAKFAST, LUNCH, DINNER }
    public enum FoodType     { VEG, NON_VEG, VEGAN }
    public enum TasteInfo    { SWEET, SPICY_LIGHT, SPICY_FULL, MILD, SAVORY }
}