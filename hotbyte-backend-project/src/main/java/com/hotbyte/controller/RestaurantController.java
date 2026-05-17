package com.hotbyte.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hotbyte.dto.request.AddCategoryRequest;
import com.hotbyte.dto.request.AddMenuItemRequest;
import com.hotbyte.dto.request.CreateRestaurantRequest;
import com.hotbyte.dto.request.UpdateOrderStatusRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.CategoryResponse;
import com.hotbyte.dto.response.MenuItemResponse;
import com.hotbyte.dto.response.OrderResponse;
import com.hotbyte.dto.response.RestaurantDashboardResponse; // ✅ FIXED IMPORT
import com.hotbyte.dto.response.RestaurantResponse;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.RestaurantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RESTAURANT','ADMIN')")
@Tag(name = "Restaurant", description = "Restaurant management APIs")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final UserRepository userRepository;

    // ── Profile ──────────────────────────────────

    @GetMapping("/profile")
    @Operation(summary = "Get my restaurant profile")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success(
                "Restaurant fetched",
                restaurantService.getMyRestaurant(getUserId(userDetails))));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update restaurant profile")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateRestaurantRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Restaurant updated",
                restaurantService.updateRestaurant(
                        getUserId(userDetails), request)));
    }

    // ── Dashboard ─────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<RestaurantDashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success(
                "Dashboard fetched",
                restaurantService.getDashboard(getUserId(userDetails))));
    }

    // ── Menu Items ────────────────────────────────

    @GetMapping("/menu")
    @Operation(summary = "Get all my menu items")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getMyMenu(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success(
                "Menu items fetched",
                restaurantService.getMyMenuItems(getUserId(userDetails))));
    }

    @PostMapping("/menu/add")
    @Operation(summary = "Add a new menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> addMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddMenuItemRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Menu item added successfully",
                restaurantService.addMenuItem(
                        getUserId(userDetails), request)));
    }

    @PutMapping("/menu/{itemId}")
    @Operation(summary = "Update a menu item")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId,
            @RequestBody AddMenuItemRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Menu item updated",
                restaurantService.updateMenuItem(
                        getUserId(userDetails), itemId, request)));
    }

    @DeleteMapping("/menu/{itemId}")
    @Operation(summary = "Delete a menu item")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {

        restaurantService.deleteMenuItem(
                getUserId(userDetails), itemId);

        return ResponseEntity.ok(ApiResponse.success(
                "Menu item deleted", null));
    }

    @PatchMapping("/menu/{itemId}/toggle")
    @Operation(summary = "Toggle item availability")
    public ResponseEntity<ApiResponse<Void>> toggleAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long itemId) {

        restaurantService.toggleItemAvailability(
                getUserId(userDetails), itemId);

        return ResponseEntity.ok(ApiResponse.success(
                "Availability toggled", null));
    }

    // ── Categories ────────────────────────────────

    @GetMapping("/categories")
    @Operation(summary = "Get my categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getMyCategories(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success(
                "Categories fetched",
                restaurantService.getMyCategories(getUserId(userDetails))));
    }

    @PostMapping("/categories/add")
    @Operation(summary = "Add a new category")
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddCategoryRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Category added",
                restaurantService.addCategory(
                        getUserId(userDetails), request)));
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId) {

        restaurantService.deleteCategory(
                getUserId(userDetails), categoryId);

        return ResponseEntity.ok(ApiResponse.success(
                "Category deleted", null));
    }

    // ── Orders ────────────────────────────────────

    @GetMapping("/orders")
    @Operation(summary = "Get active orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success(
                "Orders fetched",
                restaurantService.getMyOrders(getUserId(userDetails))));
    }

    @PutMapping("/orders/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Order status updated",
                restaurantService.updateOrderStatus(
                        getUserId(userDetails), orderId, request)));
    }

    @GetMapping("/orders/history")
    @Operation(summary = "Get all order history")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrderHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.success(
                "Order history fetched",
                restaurantService.getOrderHistory(
                        getUserId(userDetails))));
    }

    // ── Helper ────────────────────────────────────

    private Long getUserId(UserDetails userDetails) {
        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }
}