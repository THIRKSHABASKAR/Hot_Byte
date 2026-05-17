package com.hotbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                unique = true)
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType = VehicleType.BIKE;

    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;

    @Column(name = "license_number", length = 30)
    private String licenseNumber;

    @Column(name = "current_lat",
            precision = 10, scale = 8)
    private BigDecimal currentLat;

    @Column(name = "current_lng",
            precision = 11, scale = 8)
    private BigDecimal currentLng;

    @Builder.Default
    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Builder.Default
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_deliveries")
    private Integer totalDeliveries = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum VehicleType {
        BIKE, BICYCLE, CAR
    }
}