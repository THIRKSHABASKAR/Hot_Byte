package com.hotbyte.controller;

import com.hotbyte.dto.request.AddToCartRequest;
import com.hotbyte.dto.request.UpdateCartRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.CartResponse;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation
        .AuthenticationPrincipal;
import org.springframework.security.core.userdetails
        .UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart management APIs")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get my cart")
    public ResponseEntity<ApiResponse<CartResponse>>
    getCart(@AuthenticationPrincipal
            UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Cart fetched successfully",
                cartService.getCart(userId)));
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>>
    addToCart(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @Valid @RequestBody
                AddToCartRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Item added to cart",
                cartService.addToCart(userId, request)));
    }

    @PutMapping("/update")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>>
    updateCartItem(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @Valid @RequestBody
                UpdateCartRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Cart updated successfully",
                cartService.updateCartItem(
                        userId, request)));
    }

    @DeleteMapping("/remove/{cartItemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>>
    removeFromCart(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @PathVariable Long cartItemId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Item removed from cart",
                cartService.removeFromCart(
                        userId, cartItemId)));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<ApiResponse<Void>>
    clearCart(@AuthenticationPrincipal
              UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(
                "Cart cleared successfully", null));
    }

    // Helper to get userId from logged in user
    private Long getUserId(UserDetails userDetails) {
        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }
}