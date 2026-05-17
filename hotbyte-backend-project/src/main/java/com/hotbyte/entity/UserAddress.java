package com.hotbyte.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Label label = Label.HOME;

    @Column(name = "full_address", nullable = false,
            columnDefinition = "TEXT")
    private String fullAddress;

    private String city;
    private String state;
    private String pincode;
    private String landmark;

    // Fixed: BigDecimal to match DECIMAL(10,8)
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    // Fixed: BigDecimal to match DECIMAL(11,8)
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Label {
        HOME, WORK, OTHER
    }
}