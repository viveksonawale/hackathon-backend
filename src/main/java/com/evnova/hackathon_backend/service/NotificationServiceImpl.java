package com.evnova.hackathon_backend.service;

import com.evnova.hackathon_backend.dto.NotificationDTO;
import com.evnova.hackathon_backend.exception.ResourceNotFoundException;
import com.evnova.hackathon_backend.exception.UnauthorizedException;
import com.evnova.hackathon_backend.model.Notification;
import com.evnova.hackathon_backend.model.User;
import com.evnova.hackathon_backend.repository.NotificationRepository;
import com.evnova.hackathon_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    public List<NotificationDTO.Response> getMyNotifications() {
        User user = getAuthenticatedUser();
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream().map(this::map).toList();
    }

    @Override
    public void markAsRead(Long notificationId) {
        User user = getAuthenticatedUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to modify this notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User user = getAuthenticatedUser();
        List<Notification> notifications = notificationRepository.findByRecipientAndReadFalse(user);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void createForUser(Long userId, String type, String message) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        notificationRepository.save(Notification.builder()
                .recipient(recipient)
                .type(type)
                .message(message)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private NotificationDTO.Response map(Notification n) {
        return new NotificationDTO.Response(n.getId(), n.getType(), n.getMessage(), n.isRead(), n.getCreatedAt());
    }
}
