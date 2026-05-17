package com.hotbyte.controller;

import com.hotbyte.dto.request.CreateRestaurantRequest;
import com.hotbyte.dto.request.CreateRestaurantWithOwnerRequest; // ✅ FIX ADDED
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.OrderResponse;
import com.hotbyte.dto.response.RestaurantResponse;
import com.hotbyte.dto.response.UserProfileResponse;
import com.hotbyte.entity.Restaurant;
import com.hotbyte.entity.User;
import com.hotbyte.exception.BadRequestException;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.OrderRepository;
import com.hotbyte.repository.RestaurantRepository;
import com.hotbyte.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Users ─────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {

        List<UserProfileResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(
                "Users fetched", users));
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new BadRequestException("Cannot delete admin account");
        }

        userRepository.delete(user);

        return ResponseEntity.ok(ApiResponse.success(
                "User deleted", null));
    }

    // ── Restaurants ───────────────────────────────

    @GetMapping("/restaurants")
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getAllRestaurants() {

        List<RestaurantResponse> restaurants = restaurantRepository.findAll()
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(
                "Restaurants fetched", restaurants));
    }

    @PostMapping("/restaurants/create")
    @Operation(summary = "Create a restaurant with owner account")
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            @RequestBody CreateRestaurantWithOwnerRequest request) {

        if (userRepository.existsByEmail(request.getOwnerEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User owner = new User();
        owner.setName(request.getOwnerName());
        owner.setEmail(request.getOwnerEmail());
        owner.setPassword(passwordEncoder.encode(request.getOwnerPassword()));
        owner.setPhone(request.getOwnerPhone());
        owner.setRole(User.Role.RESTAURANT);
        owner.setIsActive(true);
        owner.setWalletBalance(BigDecimal.ZERO);

        User savedOwner = userRepository.save(owner);

        Restaurant restaurant = new Restaurant();
        restaurant.setUser(savedOwner);
        restaurant.setName(request.getRestaurantName());
        restaurant.setDescription(request.getDescription());
        restaurant.setLocation(request.getLocation());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setIsActive(true);
        restaurant.setAvgRating(BigDecimal.ZERO);
        restaurant.setTotalRatings(0);

        Restaurant saved = restaurantRepository.save(restaurant);

        return ResponseEntity.ok(ApiResponse.success(
                "Restaurant created successfully",
                mapToRestaurantResponse(saved)));
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    @Operation(summary = "Delete a restaurant")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(
            @PathVariable Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurantRepository.delete(restaurant);

        return ResponseEntity.ok(ApiResponse.success(
                "Restaurant deleted", null));
    }

    // ── Orders ────────────────────────────────────

    @GetMapping("/orders")
    @Operation(summary = "Get all platform orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {

        List<OrderResponse> orders = orderRepository
                .findAllByOrderByPlacedAtDesc()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(
                "All orders fetched", orders));
    }

    // ── Mappers ───────────────────────────────────

    private UserProfileResponse mapToUserResponse(User u) {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(u.getId());
        r.setName(u.getName());
        r.setEmail(u.getEmail());
        r.setPhone(u.getPhone());
        r.setWalletBalance(u.getWalletBalance());
        r.setRole(u.getRole().name());
        if (u.getGender() != null) r.setGender(u.getGender().name());
        return r;
    }

    private RestaurantResponse mapToRestaurantResponse(Restaurant r) {
        RestaurantResponse res = new RestaurantResponse();
        res.setId(r.getId());
        res.setName(r.getName());
        res.setDescription(r.getDescription());
        res.setLocation(r.getLocation());
        res.setCity(r.getCity());
        res.setPhone(r.getPhone());
        res.setImageUrl(r.getImageUrl());
        res.setAvgRating(r.getAvgRating());
        res.setIsActive(r.getIsActive());
        if (r.getUser() != null) res.setUserId(r.getUser().getId());
        return res;
    }

    private OrderResponse mapToOrderResponse(com.hotbyte.entity.Order order) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getId());
        res.setDeliveryAddress(order.getDeliveryAddress());
        res.setSubtotal(order.getSubtotal());
        res.setTotalAmount(order.getTotalAmount());
        res.setStatus(order.getStatus().name());
        res.setPlacedAt(order.getPlacedAt());
        if (order.getPaymentMethod() != null)
            res.setPaymentMethod(order.getPaymentMethod().name());
        if (order.getRestaurant() != null)
            res.setRestaurantName(order.getRestaurant().getName());
        return res;
    }
}