package com.eXiua.tasksi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eXiua.tasksi.model.Notification;
import com.eXiua.tasksi.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;

    public void send(String recipientId, String message, String type) {
        Notification notif = new Notification();
        notif.setRecipientId(recipientId);
        notif.setMessage(message);
        notif.setType(type);
        notificationRepository.save(notif);
    }

    public List<Notification> getByRecipient(String recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    public void markAsRead(Long id) {
        Notification notif = notificationRepository.findById(id).orElseThrow();
        notif.setReadFlag(true);
        notificationRepository.save(notif);
    }
}
