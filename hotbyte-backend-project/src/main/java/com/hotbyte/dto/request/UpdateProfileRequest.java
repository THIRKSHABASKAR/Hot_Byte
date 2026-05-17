package com.hotbyte.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 100,
          message = "Name must be 2 to 100 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{10}$",
             message = "Phone must be 10 digits")
    private String phone;

    private String gender;
}