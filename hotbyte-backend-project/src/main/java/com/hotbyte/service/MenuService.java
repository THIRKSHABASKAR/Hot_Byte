package com.hotbyte.service;

import com.hotbyte.dto.response.CategoryResponse;
import com.hotbyte.dto.response.MenuItemResponse;
import com.hotbyte.entity.MenuItem;

import java.util.List;

public interface MenuService {
    List<MenuItemResponse> getAllMenuItems();
    MenuItemResponse getMenuItemById(Long id);
    List<MenuItemResponse> searchMenuItems(
            String name,
            String foodType,
            Long categoryId,
            Long restaurantId);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getCategoriesByRestaurant(
            Long restaurantId);
}