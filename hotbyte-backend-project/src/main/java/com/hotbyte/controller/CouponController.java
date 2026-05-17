package com.hotbyte.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotbyte.entity.Coupon;
import com.hotbyte.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;

    /**
     * GET /coupons
     * Returns all active, non-expired coupons
     */
    @GetMapping
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        List<Coupon> active = couponRepository.findAll()
                .stream()
                .filter(Coupon::isActive)
                .filter(c -> c.getExpiryDate() == null
                          || c.getExpiryDate().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(active);
    }

    /**
     * GET /coupons/validate/{code}
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validate(@PathVariable String code) {
        return couponRepository.findByCode(code.toUpperCase())
                .filter(Coupon::isActive)
                .filter(c -> c.getExpiryDate() == null
                          || c.getExpiryDate().isAfter(LocalDateTime.now()))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid or expired coupon code.")));
    }

    /**
     * POST /coupons/apply?code=HOTBYTE20&orderAmount=499
     * Returns discount amount
     */
    @PostMapping("/apply")
    public ResponseEntity<?> apply(
            @RequestParam String code,
            @RequestParam Double orderAmount) {

        return couponRepository.findByCode(code.toUpperCase())
                .filter(Coupon::isActive)
                .map(c -> {

                    // Check expiry
                    if (c.getExpiryDate() != null
                            && LocalDateTime.now().isAfter(c.getExpiryDate())) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Coupon has expired."));
                    }

                    // Check min order amount
                    if (c.getMinOrderAmount() != null
                            && orderAmount < c.getMinOrderAmount()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message",
                                        "Min order Rs." + c.getMinOrderAmount().intValue() + " required."));
                    }

                    // Check usage limit
                    if (c.getUsageLimit() != null
                            && c.getUsedCount() >= c.getUsageLimit()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "Coupon usage limit reached."));
                    }

                    // Calculate discount
                    double discount;
                    if ("PERCENTAGE".equalsIgnoreCase(c.getDiscountType())) {
                        discount = orderAmount * c.getDiscountValue() / 100.0;
                        if (c.getMaxDiscount() != null && discount > c.getMaxDiscount()) {
                            discount = c.getMaxDiscount();
                        }
                    } else {
                        // FLAT
                        discount = c.getDiscountValue();
                    }

                    double finalAmount = Math.max(0, orderAmount - discount);

                    return ResponseEntity.ok(Map.of(
                            "discount",    discount,
                            "finalAmount", finalAmount,
                            "message",     "You saved Rs." + (int) discount + "!"
                    ));
                })
                .orElse(ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid coupon code.")));
    }
}