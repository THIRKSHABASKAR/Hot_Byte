package com.hotbyte.repository;

import com.hotbyte.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByIsActiveTrue();

    List<Coupon> findByIsActiveTrueAndOfferType(String offerType);

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    List<Coupon> findByRestaurantId(Long restaurantId);

    List<Coupon> findByMenuItemId(Long menuItemId);
}