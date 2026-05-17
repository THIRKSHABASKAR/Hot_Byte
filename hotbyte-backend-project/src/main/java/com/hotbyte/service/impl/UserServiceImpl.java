package com.hotbyte.service.impl;

import com.hotbyte.dto.request.UpdateProfileRequest;
import com.hotbyte.dto.response.UserProfileResponse;
import com.hotbyte.entity.User;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(
            Long userId,
            UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        if (request.getName() != null)
            user.setName(request.getName());
        if (request.getPhone() != null)
            user.setPhone(request.getPhone());
        if (request.getGender() != null) {
            try {
                user.setGender(User.Gender.valueOf(
                        request.getGender().toUpperCase()));
            } catch (Exception ignored) {}
        }

        userRepository.save(user);
        return mapToResponse(user);
    }

    private UserProfileResponse mapToResponse(User user) {
        UserProfileResponse response =
                new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setProfileImage(user.getProfileImage());
        response.setWalletBalance(user.getWalletBalance());
        response.setRole(user.getRole().name());
        if (user.getGender() != null)
            response.setGender(user.getGender().name());
        return response;
    }
}