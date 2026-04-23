package com.evnova.hackathon_backend.repository;

import com.evnova.hackathon_backend.model.Notification;
import com.evnova.hackathon_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndReadFalse(User recipient);
}
