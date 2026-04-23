package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.dto.NotificationDTO;
import com.evnova.hackathon_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_PARTICIPANT','ROLE_ORGANIZER')")
    public ResponseEntity<List<NotificationDTO.Response>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTICIPANT','ROLE_ORGANIZER')")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('ROLE_PARTICIPANT','ROLE_ORGANIZER')")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }
}
