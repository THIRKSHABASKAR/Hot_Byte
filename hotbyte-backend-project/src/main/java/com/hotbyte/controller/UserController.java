package com.hotbyte.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation
        .AuthenticationPrincipal;
import org.springframework.security.core.userdetails
        .UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotbyte.dto.request.UpdateProfileRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.UserProfileResponse;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profile",
     description = "User profile APIs")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    @Operation(summary = "Get my profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>>
    getProfile(@AuthenticationPrincipal
               UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Profile fetched successfully",
                userService.getProfile(userId)));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>>
    updateProfile(
            @AuthenticationPrincipal
                UserDetails userDetails,
            @Valid @RequestBody
                UpdateProfileRequest request) {
        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Profile updated successfully",
                userService.updateProfile(
                        userId, request)));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }
}