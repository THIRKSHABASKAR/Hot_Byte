package com.hotbyte.controller;

import com.hotbyte.dto.response.ApiResponse;
import com.hotbyte.dto.response.NotificationResponse;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification APIs")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get my notifications")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notifications fetched",
                        notificationService.getMyNotifications(userId)
                )
        );
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        Long userId = getUserId(userDetails);
        notificationService.markAsRead(userId, id);

        return ResponseEntity.ok(
                ApiResponse.success("Notification marked as read", null)
        );
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Unread count fetched",
                        notificationService.getUnreadCount(userId)
                )
        );
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow()
                .getId();
    }
}