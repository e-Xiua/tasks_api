package com.eXiua.tasksi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eXiua.tasksi.model.Notification;
import com.eXiua.tasksi.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*") // luego puedes restringir al dominio del frontend
public class NotificationController {

    @Autowired private NotificationService notificationService;

    // Crear una nueva notificación
    @PostMapping
    public void sendNotification(
            @RequestParam String recipientId,
            @RequestParam String message,
            @RequestParam(defaultValue = "GENERAL") String type
    ) {
        notificationService.send(recipientId, message, type);
    }

    // Listar notificaciones por usuario
    @GetMapping("/{recipientId}")
    public List<Notification> getNotificationsByRecipient(@PathVariable String recipientId) {
        return notificationService.getByRecipient(recipientId);
    }

    // Marcar una notificación como leída
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}
