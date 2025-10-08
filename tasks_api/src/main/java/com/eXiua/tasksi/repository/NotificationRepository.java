package com.eXiua.tasksi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eXiua.tasksi.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
}
