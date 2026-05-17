package com.hotbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotBlank(message = "Delivery phone is required")
    private String deliveryPhone;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private String couponCode;
}