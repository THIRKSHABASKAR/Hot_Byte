package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long cartItemId;
    private Long menuItemId;
    private String itemName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String foodType;
    private Boolean isAvailable;
}