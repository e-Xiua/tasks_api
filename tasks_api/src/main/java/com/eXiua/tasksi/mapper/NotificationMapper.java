package com.eXiua.tasksi.mapper;

import org.springframework.stereotype.Component;

import com.eXiua.tasksi.dto.NotificationDto;
import com.eXiua.tasksi.model.Notification;

@Component
public class NotificationMapper {
    public NotificationDto toDto(Notification n) {
        if (n == null) return null;
        NotificationDto d = new NotificationDto();
        d.id = n.getId();
        d.recipientId = n.getRecipientId();
        d.message = n.getMessage();
        d.type = n.getType();
        d.readFlag = n.isReadFlag();
        d.createdAt = n.getCreatedAt();
        return d;
    }

    public Notification toEntity(NotificationDto d) {
        if (d == null) return null;
        Notification n = new Notification();
        n.setId(d.id);
        n.setRecipientId(d.recipientId);
        n.setMessage(d.message);
        n.setType(d.type);
        n.setReadFlag(d.readFlag);
        n.setCreatedAt(d.createdAt != null ? d.createdAt : java.time.LocalDateTime.now());
        return n;
    }
}
