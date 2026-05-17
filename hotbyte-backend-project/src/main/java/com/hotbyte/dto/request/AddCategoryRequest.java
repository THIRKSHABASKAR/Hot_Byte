package com.hotbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String imageUrl;
}