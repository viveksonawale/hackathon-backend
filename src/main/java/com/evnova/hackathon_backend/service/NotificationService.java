package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO.Response> getMyNotifications();
    void markAsRead(Long notificationId);
    void markAllAsRead();
    void createForUser(Long userId, String type, String message);
}
