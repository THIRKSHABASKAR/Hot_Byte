package com.hotbyte.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartRequest {

    @NotNull(message = "Cart item ID is required")
    private Long cartItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}