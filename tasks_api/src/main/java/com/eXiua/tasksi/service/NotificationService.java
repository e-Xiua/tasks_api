package com.eXiua.tasksi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eXiua.tasksi.dto.NotificationDto;
import com.eXiua.tasksi.mapper.NotificationMapper;
import com.eXiua.tasksi.model.Notification;
import com.eXiua.tasksi.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationMapper notificationMapper;

    public void send(String recipientId, String message, String type) {
        Notification notif = new Notification();
        notif.setRecipientId(recipientId);
        notif.setMessage(message);
        notif.setType(type);
        notificationRepository.save(notif);
    }

    public org.springframework.data.domain.Page<NotificationDto> getByRecipient(String recipientId, int page, int size) {
        org.springframework.data.domain.Pageable p = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId, p).map(notificationMapper::toDto);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notif = notificationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notif.setReadFlag(true);
        notificationRepository.save(notif);
    }
}
