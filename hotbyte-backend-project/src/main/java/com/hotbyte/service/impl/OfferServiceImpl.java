package com.hotbyte.service.impl;

import com.hotbyte.dto.request.CreateCouponRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.CouponResponse;
import com.hotbyte.entity.Coupon;
import com.hotbyte.exception.BadRequestException;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.CouponRepository;
import com.hotbyte.repository.RestaurantRepository;
import com.hotbyte.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final CouponRepository couponRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public List<CouponResponse> getAllActiveOffers() {
        return couponRepository.findByIsActiveTrue()
                .stream()
                .filter(c -> c.getExpiryDate() == null || c.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponse> getOffersByType(String offerType) {
        return couponRepository.findByIsActiveTrueAndOfferType(offerType)
                .stream()
                .filter(c -> c.getExpiryDate() == null || c.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + code));

        if (!coupon.isActive()) {
            throw new BadRequestException("This coupon is no longer active.");
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This coupon has expired.");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BadRequestException("This coupon has reached its usage limit.");
        }

        return toResponse(coupon);
    }

    @Override
    public ApiResponse applyCoupon(String code, Double orderAmount) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid coupon code."));

        if (!coupon.isActive()) throw new BadRequestException("Coupon is inactive.");
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Coupon has expired.");
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit())
            throw new BadRequestException("Coupon usage limit reached.");
        if (coupon.getMinOrderAmount() != null && orderAmount < coupon.getMinOrderAmount())
            throw new BadRequestException("Minimum order amount is ₹" + coupon.getMinOrderAmount().intValue());

        double discount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = (orderAmount * coupon.getDiscountValue()) / 100;
            if (coupon.getMaxDiscount() != null) {
                discount = Math.min(discount, coupon.getMaxDiscount());
            }
        } else {
            discount = coupon.getDiscountValue();
        }

        double finalAmount = orderAmount - discount;

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return ApiResponse.builder()
                .success(true)
                .message("Coupon applied! You saved ₹" + String.format("%.0f", discount))
                .data(java.util.Map.of(
                        "discount", discount,
                        "finalAmount", finalAmount,
                        "savingsText", "You saved ₹" + String.format("%.0f", discount)
                ))
                .build();
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {
        if (couponRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new BadRequestException("Coupon code already exists: " + request.getCode());
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .title(request.getTitle())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .expiryDate(request.getExpiryDate())
                .isActive(true)
                .offerType(request.getOfferType())
                .badgeColor(request.getBadgeColor() != null ? request.getBadgeColor() : "orange")
                .imageUrl(request.getImageUrl())
                .build();

        return toResponse(couponRepository.save(coupon));
    }

    @Override
    public ApiResponse toggleCouponStatus(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        coupon.setActive(!coupon.isActive());
        couponRepository.save(coupon);
        return ApiResponse.builder()
                .success(true)
                .message("Coupon " + (coupon.isActive() ? "activated" : "deactivated") + " successfully.")
                .build();
    }

    @Override
    public ApiResponse deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        couponRepository.delete(coupon);
        return ApiResponse.builder().success(true).message("Coupon deleted successfully.").build();
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CouponResponse toResponse(Coupon coupon) {
        boolean isExpired = coupon.getExpiryDate() != null
                && coupon.getExpiryDate().isBefore(LocalDateTime.now());

        Long daysRemaining = null;
        if (coupon.getExpiryDate() != null && !isExpired) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), coupon.getExpiryDate());
        }

        String savingsText;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            savingsText = coupon.getMaxDiscount() != null
                    ? "Save up to ₹" + coupon.getMaxDiscount().intValue()
                    : "Save " + coupon.getDiscountValue().intValue() + "% off";
        } else {
            savingsText = "Save ₹" + coupon.getDiscountValue().intValue() + " flat";
        }

        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .title(coupon.getTitle())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscount(coupon.getMaxDiscount())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .expiryDate(coupon.getExpiryDate())
                .isActive(coupon.isActive())
                .isExpired(isExpired)
                .offerType(coupon.getOfferType())
                .badgeColor(coupon.getBadgeColor())
                .imageUrl(coupon.getImageUrl())
                .savingsText(savingsText)
                .daysRemaining(daysRemaining)
                .build();
    }
}