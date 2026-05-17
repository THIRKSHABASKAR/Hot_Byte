package com.hotbyte.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRestaurantWithOwnerRequest {

    // Owner account details
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Owner email is required")
    @Email(message = "Invalid email format")
    private String ownerEmail;

    @NotBlank(message = "Password is required")
    private String ownerPassword;

    private String ownerPhone;

    // Restaurant details
    @NotBlank(message = "Restaurant name is required")
    private String restaurantName;

    private String description;
    private String location;
    private String city;
    private String phone;
}