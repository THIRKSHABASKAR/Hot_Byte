package com.hotbyte.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Address is required")
    private String fullAddress;

    private String label;
    private String city;
    private String state;
    private String pincode;
    private String landmark;
    private Boolean isDefault = false;
}