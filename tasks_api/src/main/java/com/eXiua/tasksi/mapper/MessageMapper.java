package com.eXiua.tasksi.mapper;

import org.springframework.stereotype.Component;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.model.Message;

@Component
public class MessageMapper {
    public MessageDto toDto(Message m) {
        if (m == null) return null;
        MessageDto d = new MessageDto();
        d.id = m.getId();
        d.taskId = m.getTask() != null ? m.getTask().getId() : null;
        d.senderId = m.getSenderId();
        d.receiverId = m.getReceiverId();
        d.content = m.getContent();
        d.timestamp = m.getTimestamp();
        d.readFlag = m.isReadFlag();
        return d;
    }

    public Message toEntity(MessageDto d) {
        if (d == null) return null;
        Message m = new Message();
        m.setId(d.id);
        // task should be set by service if needed
        m.setSenderId(d.senderId);
        m.setReceiverId(d.receiverId);
        m.setContent(d.content);
        m.setTimestamp(d.timestamp != null ? d.timestamp : java.time.LocalDateTime.now());
        m.setReadFlag(d.readFlag);
        return m;
    }
}
