package com.hotbyte.service.impl;

import com.hotbyte.dto.response.CategoryResponse;
import com.hotbyte.dto.response.MenuItemResponse;
import com.hotbyte.entity.MenuCategory;
import com.hotbyte.entity.MenuItem;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.MenuCategoryRepository;
import com.hotbyte.repository.MenuItemRepository;
import com.hotbyte.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;

    @Override
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found"));
        return mapToMenuItemResponse(item);
    }

    @Override
    public List<MenuItemResponse> searchMenuItems(
            String name, String foodType,
            Long categoryId, Long restaurantId) {

        MenuItem.FoodType type = null;
        if (foodType != null && !foodType.isEmpty()) {
            try {
                type = MenuItem.FoodType.valueOf(
                        foodType.toUpperCase());
            } catch (IllegalArgumentException e) {
                type = null;
            }
        }

        return menuItemRepository.searchMenuItems(
                name, type, categoryId, restaurantId)
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getCategoriesByRestaurant(
            Long restaurantId) {
        return categoryRepository
                .findByRestaurantIdAndIsActiveTrue(restaurantId)
                .stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    // Map MenuItem entity to response DTO
    private MenuItemResponse mapToMenuItemResponse(
            MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setIngredients(item.getIngredients());
        response.setPrice(item.getPrice());
        response.setDiscountPrice(item.getDiscountPrice());
        response.setImageUrl(item.getImageUrl());
        response.setIsAvailable(item.getIsAvailable());
        response.setAvgRating(item.getAvgRating());
        response.setTotalRatings(item.getTotalRatings());
        response.setCookingTime(item.getCookingTime());
        response.setCalories(item.getCalories());
        response.setFats(item.getFats());
        response.setProteins(item.getProteins());
        response.setCarbohydrates(item.getCarbohydrates());

        if (item.getFoodType() != null)
            response.setFoodType(
                    item.getFoodType().name());
        if (item.getAvailability() != null)
            response.setAvailability(
                    item.getAvailability().name());
        if (item.getTasteInfo() != null)
            response.setTasteInfo(
                    item.getTasteInfo().name());
        if (item.getRestaurant() != null) {
            response.setRestaurantId(
                    item.getRestaurant().getId());
            response.setRestaurantName(
                    item.getRestaurant().getName());
        }
        if (item.getCategory() != null) {
            response.setCategoryId(
                    item.getCategory().getId());
            response.setCategoryName(
                    item.getCategory().getName());
        }
        return response;
    }

    // Map MenuCategory entity to response DTO
    private CategoryResponse mapToCategoryResponse(
            MenuCategory category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        if (category.getRestaurant() != null) {
            response.setRestaurantId(
                    category.getRestaurant().getId());
            response.setRestaurantName(
                    category.getRestaurant().getName());
        }
        return response;
    }
}