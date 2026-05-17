package com.hotbyte.service.impl;

import com.hotbyte.dto.response.NotificationResponse;
import com.hotbyte.entity.Notification;
import com.hotbyte.entity.User;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.NotificationRepository;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository
            notificationRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationResponse> getMyNotifications(
            Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long userId,
            Long notificationId) {
        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Notification not found");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationRepository
                .countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void createNotification(Long userId,
            String title, String message,
            Notification.NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(
            Notification n) {
        NotificationResponse response =
                new NotificationResponse();
        response.setId(n.getId());
        response.setTitle(n.getTitle());
        response.setMessage(n.getMessage());
        response.setType(n.getType().name());
        response.setIsRead(n.getIsRead());
        response.setCreatedAt(n.getCreatedAt());
        return response;
    }
}