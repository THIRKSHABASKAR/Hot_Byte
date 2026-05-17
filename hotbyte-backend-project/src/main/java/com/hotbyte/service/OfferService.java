package com.hotbyte.service;

import com.hotbyte.dto.request.CreateCouponRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.CouponResponse;

import java.util.List;

public interface OfferService {

    List<CouponResponse> getAllActiveOffers();

    List<CouponResponse> getOffersByType(String offerType);

    CouponResponse validateCoupon(String code);

    ApiResponse applyCoupon(String code, Double orderAmount);

    CouponResponse createCoupon(CreateCouponRequest request);

    ApiResponse toggleCouponStatus(Long id);

    ApiResponse deleteCoupon(Long id);

    List<CouponResponse> getAllCoupons();
}