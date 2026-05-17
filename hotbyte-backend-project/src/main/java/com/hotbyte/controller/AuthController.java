package com.hotbyte.controller;

import com.hotbyte.dto.request.LoginRequest;
import java.util.Map;
import com.hotbyte.dto.request.RegisterRequest;
import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.AuthResponse;
import com.hotbyte.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication",
     description = "Register and Login APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>>
    register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response =
                authService.register(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Registration successful",
                        response));
    }
    
    @PostMapping("/google")
    @Operation(summary = "Login with Google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(
            @RequestBody Map<String, String> body) {

        AuthResponse response = authService.googleLogin(body.get("token"));

        return ResponseEntity.ok(
                ApiResponse.success("Google login successful", response)
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>>
    login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response =
                authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response));
    }
}