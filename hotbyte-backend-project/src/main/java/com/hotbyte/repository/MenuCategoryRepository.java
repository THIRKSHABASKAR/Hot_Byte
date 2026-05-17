package com.hotbyte.repository;

import com.hotbyte.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuCategoryRepository
        extends JpaRepository<MenuCategory, Long> {

    // Get all categories by restaurant
    List<MenuCategory> findByRestaurantId(
            Long restaurantId);

    // Get only active categories
    List<MenuCategory> findByIsActiveTrue();

    // Get active categories by restaurant
    List<MenuCategory> findByRestaurantIdAndIsActiveTrue(
            Long restaurantId);

    // Check if category name exists for restaurant
    boolean existsByNameAndRestaurantId(
            String name, Long restaurantId);
}