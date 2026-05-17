package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private String ingredients;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String imageUrl;
    private String availability;
    private String foodType;
    private String tasteInfo;
    private Integer calories;
    private BigDecimal fats;
    private BigDecimal proteins;
    private BigDecimal carbohydrates;
    private Integer cookingTime;
    private BigDecimal avgRating;
    private Integer totalRatings;
    private Boolean isAvailable;
    private Long restaurantId;
    private String restaurantName;
    private Long categoryId;
    private String categoryName;
}