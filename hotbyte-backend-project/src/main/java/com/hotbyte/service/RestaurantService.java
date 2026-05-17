package com.hotbyte.service;

import com.hotbyte.dto.request.*;
import com.hotbyte.dto.response.*;
import java.util.List;

public interface RestaurantService {

    // Restaurant Profile
    RestaurantResponse getMyRestaurant(Long userId);
    RestaurantResponse updateRestaurant(
            Long userId,
            CreateRestaurantRequest request);

    // Dashboard Stats
    RestaurantDashboardResponse getDashboard(
            Long userId);

    // Menu Items
    MenuItemResponse addMenuItem(
            Long userId,
            AddMenuItemRequest request);
    MenuItemResponse updateMenuItem(
            Long userId, Long itemId,
            AddMenuItemRequest request);
    void deleteMenuItem(Long userId, Long itemId);
    void toggleItemAvailability(
            Long userId, Long itemId);
    List<MenuItemResponse> getMyMenuItems(Long userId);

    // Categories
    CategoryResponse addCategory(
            Long userId,
            AddCategoryRequest request);
    void deleteCategory(Long userId, Long categoryId);
    List<CategoryResponse> getMyCategories(Long userId);

    // Orders
    List<OrderResponse> getMyOrders(Long userId);
    OrderResponse updateOrderStatus(
            Long userId, Long orderId,
            UpdateOrderStatusRequest request);
    List<OrderResponse> getOrderHistory(Long userId);
}