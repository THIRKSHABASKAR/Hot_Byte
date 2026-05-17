package com.hotbyte.service;

import com.hotbyte.dto.request.UpdateProfileRequest;
import com.hotbyte.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId,
            UpdateProfileRequest request);
}