package com.hotbyte.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotbyte.dto.request.AddToCartRequest;
import com.hotbyte.dto.request.UpdateCartRequest;
import com.hotbyte.dto.response.CartItemResponse;
import com.hotbyte.dto.response.CartResponse;
import com.hotbyte.entity.Cart;
import com.hotbyte.entity.CartItem;
import com.hotbyte.entity.MenuItem;
import com.hotbyte.entity.User;
import com.hotbyte.exception.BadRequestException;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.CartItemRepository;
import com.hotbyte.repository.CartRepository;
import com.hotbyte.repository.MenuItemRepository;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId,
            AddToCartRequest request) {

        // Validate menu item exists and is available
        MenuItem menuItem = menuItemRepository
                .findById(request.getMenuItemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Menu item not found"));

        if (!menuItem.getIsAvailable()) {
            throw new BadRequestException(
                    "Sorry, this item is currently"
                    + " not available");
        }

        Cart cart = getOrCreateCart(userId);

        // Check if item already in cart
        Optional<CartItem> existingItem =
                cartItemRepository
                        .findByCartIdAndMenuItemId(
                                cart.getId(),
                                menuItem.getId());

        if (existingItem.isPresent()) {
            // Update quantity if already exists
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity()
                    + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            // Add new item to cart
            BigDecimal price =
                    menuItem.getDiscountPrice() != null
                    ? menuItem.getDiscountPrice()
                    : menuItem.getPrice();

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .unitPrice(price)
                    .build();
            cartItemRepository.save(newItem);
        }

        // Refresh cart
        cart = cartRepository.findById(cart.getId())
                .orElseThrow();
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId,
            UpdateCartRequest request) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository
                .findById(request.getCartItemId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found"));

        // Make sure item belongs to this user's cart
        if (!cartItem.getCart().getId()
                .equals(cart.getId())) {
            throw new BadRequestException(
                    "Cart item does not belong"
                    + " to your cart");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        cart = cartRepository.findById(cart.getId())
                .orElseThrow();
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long userId,
            Long cartItemId) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found"));

        if (!cartItem.getCart().getId()
                .equals(cart.getId())) {
            throw new BadRequestException(
                    "Cart item does not belong"
                    + " to your cart");
        }

        cartItemRepository.delete(cartItem);

        cart = cartRepository.findById(cart.getId())
                .orElseThrow();
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        // delete from DB
        cartItemRepository.deleteByCartId(cart.getId());

        // 🔥 FIX: clear in-memory list
        if (cart.getCartItems() != null) {
            cart.getCartItems().clear();
        }
    }

    // Get existing cart or create new one
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found"));
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    // Map Cart to CartResponse
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());

        List<CartItemResponse> items =
                cart.getCartItems() == null
                ? List.of()
                : cart.getCartItems().stream()
                        .map(this::mapToCartItemResponse)
                        .collect(Collectors.toList());

        response.setItems(items);
        response.setTotalItems(items.size());

        // Calculate subtotal
        BigDecimal subtotal = items.stream()
                .map(i -> i.getUnitPrice().multiply(
                        BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = new BigDecimal("40.00");
        response.setSubtotal(subtotal);
        response.setDeliveryFee(deliveryFee);
        response.setTotal(subtotal.add(deliveryFee));
        return response;
    }

    // Map CartItem to CartItemResponse
    private CartItemResponse mapToCartItemResponse(
            CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(item.getId());
        response.setMenuItemId(item.getMenuItem().getId());
        response.setItemName(item.getMenuItem().getName());
        response.setImageUrl(
                item.getMenuItem().getImageUrl());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(
                item.getUnitPrice().multiply(
                        BigDecimal.valueOf(
                                item.getQuantity())));
        response.setFoodType(
                item.getMenuItem().getFoodType() != null
                ? item.getMenuItem().getFoodType().name()
                : null);
        response.setIsAvailable(
                item.getMenuItem().getIsAvailable());
        return response;
    }
}