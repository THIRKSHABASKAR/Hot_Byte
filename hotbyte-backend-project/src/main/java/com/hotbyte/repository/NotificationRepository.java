package com.hotbyte.repository;

import com.hotbyte.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get all notifications for user
    List<Notification> findByUserIdOrderByCreatedAtDesc(
            Long userId);

    // Get unread notifications
    List<Notification> findByUserIdAndIsReadFalse(
            Long userId);

    // Count unread notifications
    int countByUserIdAndIsReadFalse(Long userId);
}