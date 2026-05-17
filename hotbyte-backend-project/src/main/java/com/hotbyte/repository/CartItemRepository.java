package com.hotbyte.repository;

import com.hotbyte.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    // Get all items in a cart
    List<CartItem> findByCartId(Long cartId);

    // Find specific item in cart
    Optional<CartItem> findByCartIdAndMenuItemId(
            Long cartId, Long menuItemId);

    // Delete all items in a cart
    void deleteByCartId(Long cartId);

    // Count items in cart
    int countByCartId(Long cartId);
}