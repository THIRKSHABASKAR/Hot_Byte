package com.hotbyte.dto.response;

import lombok.Data;

@Data
public class AddressResponse {
    private Long id;
    private String label;
    private String fullAddress;
    private String city;
    private String state;
    private String pincode;
    private String landmark;
    private Boolean isDefault;
}