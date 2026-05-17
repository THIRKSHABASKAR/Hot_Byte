package com.hotbyte.controller;

import com.hotbyte.dto.request.PlaceOrderRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.OrderResponse;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.OrderService;
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

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders",
     description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping("/place")
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderResponse>>
    placeOrder(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @Valid @RequestBody
                PlaceOrderRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Order placed successfully",
                orderService.placeOrder(userId, request)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my order history")
    public ResponseEntity<ApiResponse<List<OrderResponse>>>
    getMyOrders(@AuthenticationPrincipal
                UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Orders fetched successfully",
                orderService.getMyOrders(userId)));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<ApiResponse<OrderResponse>>
    getOrderById(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Order fetched successfully",
                orderService.getOrderById(
                        userId, orderId)));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>>
    cancelOrder(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @PathVariable Long orderId) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Order cancelled successfully",
                orderService.cancelOrder(
                        userId, orderId)));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }
}