package com.hotbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                unique = true)
    private User user;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;
    private String city;
    private String pincode;

    // Fixed: BigDecimal to match DECIMAL(10,8)
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    // Fixed: BigDecimal to match DECIMAL(11,8)
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    private String phone;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "restaurant",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<MenuCategory> categories;

    @OneToMany(mappedBy = "restaurant",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<MenuItem> menuItems;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}