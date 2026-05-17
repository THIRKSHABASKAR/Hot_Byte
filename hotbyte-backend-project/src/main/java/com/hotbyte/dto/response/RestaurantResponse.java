package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String city;
    private String pincode;
    private String phone;
    private String imageUrl;
    private BigDecimal avgRating;
    private Integer totalRatings;
    private Boolean isActive;
    private String openingTime;
    private String closingTime;
    private Long userId;
}