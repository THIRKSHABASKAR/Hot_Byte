package com.hotbyte.repository;

import com.hotbyte.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    // Get all orders by user
    List<Order> findByUserIdOrderByPlacedAtDesc(
            Long userId);

    // Get all orders by restaurant
    List<Order> findByRestaurantIdOrderByPlacedAtDesc(
            Long restaurantId);

    // Get orders by status
    List<Order> findByStatus(Order.OrderStatus status);

    // Get orders by user and status
    List<Order> findByUserIdAndStatus(
            Long userId, Order.OrderStatus status);

    // Count orders by user
    int countByUserId(Long userId);

    // Get all orders ordered by date
    List<Order> findAllByOrderByPlacedAtDesc();
}