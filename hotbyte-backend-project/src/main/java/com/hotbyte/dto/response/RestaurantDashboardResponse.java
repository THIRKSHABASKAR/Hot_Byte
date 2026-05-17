package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RestaurantDashboardResponse {
    private Long restaurantId;
    private String restaurantName;
    private String imageUrl;
    private int totalMenuItems;
    private int totalOrders;
    private int pendingOrders;
    private int completedOrders;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private int totalCategories;
}