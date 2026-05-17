package com.hotbyte.repository;

import com.hotbyte.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    // Get reviews by restaurant
    List<Review> findByRestaurantIdAndIsVisibleTrue(
            Long restaurantId);

    // Get reviews by menu item
    List<Review> findByMenuItemIdAndIsVisibleTrue(
            Long menuItemId);

    // Get reviews by user
    List<Review> findByUserId(Long userId);

    // Check if user already reviewed this order
    boolean existsByUserIdAndOrderId(
            Long userId, Long orderId);

    // Get average rating for restaurant
    @Query("SELECT AVG(r.rating) FROM Review r " +
           "WHERE r.restaurant.id = :restaurantId " +
           "AND r.isVisible = true")
    Double getAverageRatingForRestaurant(
            @Param("restaurantId") Long restaurantId);

    // Get average rating for menu item
    @Query("SELECT AVG(r.rating) FROM Review r " +
           "WHERE r.menuItem.id = :menuItemId " +
           "AND r.isVisible = true")
    Double getAverageRatingForMenuItem(
            @Param("menuItemId") Long menuItemId);
}