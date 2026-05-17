package com.hotbyte.controller;

import com.hotbyte.dto.request.CreateCouponRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.CouponResponse;
import com.hotbyte.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    // Public - anyone can view active offers
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllActiveOffers() {
        return ResponseEntity.ok(offerService.getAllActiveOffers());
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        return ResponseEntity.ok(offerService.getOffersByType("COUPON"));
    }

    @GetMapping("/discounted-items")
    public ResponseEntity<List<CouponResponse>> getDiscountedItems() {
        return ResponseEntity.ok(offerService.getOffersByType("ITEM_DISCOUNT"));
    }

    @GetMapping("/restaurant-deals")
    public ResponseEntity<List<CouponResponse>> getRestaurantDeals() {
        return ResponseEntity.ok(offerService.getOffersByType("RESTAURANT_DEAL"));
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<CouponResponse> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(offerService.validateCoupon(code));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> applyCoupon(
            @RequestParam String code,
            @RequestParam Double orderAmount) {
        return ResponseEntity.ok(offerService.applyCoupon(code, orderAmount));
    }

    // Admin only - manage coupons
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(offerService.createCoupon(request));
    }

    @PutMapping("/admin/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> toggleCouponStatus(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.toggleCouponStatus(id));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.deleteCoupon(id));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(offerService.getAllCoupons());
    }
}