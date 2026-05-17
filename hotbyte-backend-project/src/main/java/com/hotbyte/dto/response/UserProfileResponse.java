package com.hotbyte.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String profileImage;
    private BigDecimal walletBalance;
    private String role;
}