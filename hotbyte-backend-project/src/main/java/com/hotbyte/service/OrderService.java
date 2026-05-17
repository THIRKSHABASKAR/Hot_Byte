package com.hotbyte.service;

import com.hotbyte.dto.request.PlaceOrderRequest;
import com.hotbyte.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(Long userId,
            PlaceOrderRequest request);
    List<OrderResponse> getMyOrders(Long userId);
    OrderResponse getOrderById(Long userId, Long orderId);
    OrderResponse cancelOrder(Long userId, Long orderId);
}