package com.hotbyte.repository;

import com.hotbyte.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    // Get all items by restaurant
    List<MenuItem> findByRestaurantId(Long restaurantId);

    // Get all items by category
    List<MenuItem> findByCategoryId(Long categoryId);

    // Get all available items
    List<MenuItem> findByIsAvailableTrue();

    // Get items by food type (VEG/NON_VEG/VEGAN)
    List<MenuItem> findByFoodTypeAndIsAvailableTrue(
            MenuItem.FoodType foodType);

    // Search by name (case insensitive)
    List<MenuItem> findByNameContainingIgnoreCaseAndIsAvailableTrue(
            String name);

    // Get items by restaurant and category
    List<MenuItem> findByRestaurantIdAndCategoryId(
            Long restaurantId, Long categoryId);

    // Get items by availability time
    List<MenuItem> findByAvailabilityAndIsAvailableTrue(
            MenuItem.Availability availability);

    // Search with multiple filters
    @Query("SELECT m FROM MenuItem m WHERE " +
           "(:name IS NULL OR LOWER(m.name) " +
           "LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:foodType IS NULL OR m.foodType = :foodType) AND " +
           "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
           "(:restaurantId IS NULL OR m.restaurant.id = :restaurantId) AND " +
           "m.isAvailable = true")
    List<MenuItem> searchMenuItems(
            @Param("name") String name,
            @Param("foodType") MenuItem.FoodType foodType,
            @Param("categoryId") Long categoryId,
            @Param("restaurantId") Long restaurantId);
}