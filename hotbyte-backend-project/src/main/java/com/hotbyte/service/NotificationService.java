package com.hotbyte.service;

import com.hotbyte.dto.response.NotificationResponse;
import com.hotbyte.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications(
            Long userId);
    void markAsRead(Long userId, Long notificationId);
    int getUnreadCount(Long userId);
    void createNotification(Long userId, String title,
            String message,
            Notification.NotificationType type);
}