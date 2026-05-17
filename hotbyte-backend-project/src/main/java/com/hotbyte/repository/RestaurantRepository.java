package com.hotbyte.repository;

import com.hotbyte.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository
        extends JpaRepository<Restaurant, Long> {

    // Find restaurant by user id
    Optional<Restaurant> findByUserId(Long userId);

    // Get all active restaurants
    List<Restaurant> findByIsActiveTrue();

    // Search restaurants by name
    List<Restaurant> findByNameContainingIgnoreCaseAndIsActiveTrue(
            String name);

    // Find by city
    List<Restaurant> findByCityIgnoreCaseAndIsActiveTrue(
            String city);
}