package com.hotbyte.repository;

import com.hotbyte.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUsageRepository
        extends JpaRepository<CouponUsage, Long> {
    boolean existsByCouponIdAndUserId(
            Long couponId, Long userId);
}