package com.hotbyte.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotbyte.dto.request.PlaceOrderRequest;
import com.hotbyte.dto.response.OrderItemResponse;
import com.hotbyte.dto.response.OrderResponse;
import com.hotbyte.entity.Cart;
import com.hotbyte.entity.Coupon;
import com.hotbyte.entity.Notification;
import com.hotbyte.entity.Order;
import com.hotbyte.entity.OrderItem;
import com.hotbyte.entity.Restaurant;
import com.hotbyte.entity.User;
import com.hotbyte.exception.BadRequestException;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.CartItemRepository;
import com.hotbyte.repository.CartRepository;
import com.hotbyte.repository.CouponRepository;
import com.hotbyte.repository.OrderRepository;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.CartService;
import com.hotbyte.service.NotificationService;
import com.hotbyte.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CartService cartService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(
                        "Your cart is empty. Please add items first."));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty. Please add items first.");
        }

        // Get restaurant from first cart item
        Restaurant restaurant = cart.getCartItems()
                .get(0).getMenuItem().getRestaurant();

        // Calculate subtotal
        BigDecimal subtotal = cart.getCartItems()
                .stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = new BigDecimal("40.00");
        BigDecimal couponDiscount = BigDecimal.ZERO;
        Coupon appliedCoupon = null;

        // Apply coupon if provided
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            appliedCoupon = couponRepository
                    .findByCode(request.getCouponCode().toUpperCase())
                    .orElseThrow(() -> new BadRequestException("Invalid coupon code"));

            couponDiscount = calculateDiscount(appliedCoupon, subtotal);
        }

        BigDecimal totalAmount = subtotal.add(deliveryFee).subtract(couponDiscount);

        // Build order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryPhone(request.getDeliveryPhone());
        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setCouponDiscount(couponDiscount);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PLACED);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setCoupon(appliedCoupon);
        order.setEstimatedDelivery(LocalDateTime.now().plusMinutes(45));

        try {
            order.setPaymentMethod(
                    Order.PaymentMethod.valueOf(
                            request.getPaymentMethod().toUpperCase()));
        } catch (Exception e) {
            order.setPaymentMethod(Order.PaymentMethod.COD);
        }

        Order savedOrder = orderRepository.save(order);

        // Save order items from cart
        List<OrderItem> orderItems = cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(savedOrder);
                    orderItem.setMenuItem(cartItem.getMenuItem());
                    orderItem.setItemName(cartItem.getMenuItem().getName());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setTotalPrice(
                            cartItem.getUnitPrice()
                                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());

        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);

        // Clear cart after order placed
        cartService.clearCart(userId);

        // Send notification
        notificationService.createNotification(
                userId,
                "Order Placed Successfully! 🎉",
                "Your order #" + savedOrder.getId()
                        + " has been placed. Estimated delivery: 45 minutes.",
                Notification.NotificationType.ORDER_PLACED);

        logger.info("Order placed: {} by user: {}", savedOrder.getId(), userId);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository
                .findByUserIdOrderByPlacedAtDesc(userId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to view this order");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this order");
        }

        if (!order.getStatus().equals(Order.OrderStatus.PLACED)) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason("Cancelled by customer");
        orderRepository.save(order);

        notificationService.createNotification(
                userId,
                "Order Cancelled",
                "Your order #" + orderId + " has been cancelled.",
                Notification.NotificationType.ORDER_CANCELLED);

        return mapToOrderResponse(order);
    }

    // ── Calculate coupon discount ─────────────────────────────────────────────
    // Uses Coupon.java fields: isActive (boolean), expiryDate, minOrderAmount,
    // discountType (String), discountValue (Double), maxDiscount (Double)
    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {

        if (!coupon.isActive()) {
            throw new BadRequestException("This coupon is no longer active");
        }

        if (coupon.getExpiryDate() != null
                && LocalDateTime.now().isAfter(coupon.getExpiryDate())) {
            throw new BadRequestException("This coupon has expired");
        }

        if (coupon.getMinOrderAmount() != null
                && subtotal.compareTo(
                        BigDecimal.valueOf(coupon.getMinOrderAmount())) < 0) {
            throw new BadRequestException(
                    "Minimum order amount for this coupon is ₹"
                            + coupon.getMinOrderAmount().intValue());
        }

        BigDecimal discount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = subtotal
                    .multiply(BigDecimal.valueOf(coupon.getDiscountValue()))
                    .divide(BigDecimal.valueOf(100));
            // Apply max discount cap
            if (coupon.getMaxDiscount() != null
                    && discount.compareTo(
                            BigDecimal.valueOf(coupon.getMaxDiscount())) > 0) {
                discount = BigDecimal.valueOf(coupon.getMaxDiscount());
            }
        } else {
            // FLAT
            discount = BigDecimal.valueOf(coupon.getDiscountValue());
        }

        return discount;
    }

    // ── Map Order → OrderResponse ─────────────────────────────────────────────
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setDeliveryPhone(order.getDeliveryPhone());
        response.setSubtotal(order.getSubtotal());
        response.setDeliveryFee(order.getDeliveryFee());
        response.setCouponDiscount(order.getCouponDiscount());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setPlacedAt(order.getPlacedAt());
        response.setEstimatedDelivery(order.getEstimatedDelivery());
        response.setDeliveredAt(order.getDeliveredAt());

        if (order.getPaymentMethod() != null)
            response.setPaymentMethod(order.getPaymentMethod().name());
        if (order.getPaymentStatus() != null)
            response.setPaymentStatus(order.getPaymentStatus().name());
        if (order.getRestaurant() != null) {
            response.setRestaurantName(order.getRestaurant().getName());
            response.setRestaurantImage(order.getRestaurant().getImageUrl());
        }
        if (order.getOrderItems() != null) {
            response.setItems(order.getOrderItems()
                    .stream()
                    .map(this::mapToOrderItemResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setMenuItemId(item.getMenuItem().getId());
        response.setItemName(item.getItemName());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }
}