package com.hotbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    private String city;
    private String pincode;
    private String phone;
    private String imageUrl;
    private String openingTime;
    private String closingTime;
}