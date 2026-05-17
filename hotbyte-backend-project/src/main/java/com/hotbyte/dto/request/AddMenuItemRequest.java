package com.hotbyte.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AddMenuItemRequest {

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;
    private String ingredients;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal discountPrice;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String imageUrl;
    private String availability;
    private String foodType;
    private String tasteInfo;
    private Integer calories;
    private BigDecimal fats;
    private BigDecimal proteins;
    private BigDecimal carbohydrates;
    private Integer cookingTime;
}