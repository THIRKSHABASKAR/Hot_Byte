package com.hotbyte.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
          message = "Name must be 2 to 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6,
          message = "Password must be at least 6 characters")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$",
             message = "Phone must be 10 digits")
    private String phone;

    private String gender;
    private String address;
}