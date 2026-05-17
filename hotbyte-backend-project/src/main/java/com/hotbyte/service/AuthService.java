package com.hotbyte.service;

import com.hotbyte.dto.request.LoginRequest;
import com.hotbyte.dto.request.RegisterRequest;
import com.hotbyte.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
    
    AuthResponse googleLogin(String token);
}