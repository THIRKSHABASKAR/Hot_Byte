package com.hotbyte.service;

import com.hotbyte.dto.request.AddToCartRequest;
import com.hotbyte.dto.request.UpdateCartRequest;
import com.hotbyte.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addToCart(Long userId,
            AddToCartRequest request);
    CartResponse updateCartItem(Long userId,
            UpdateCartRequest request);
    CartResponse removeFromCart(Long userId,
            Long cartItemId);
    void clearCart(Long userId);
}