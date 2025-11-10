package com.eXiua.tasksi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired private MessageService messageService;

    @PostMapping("/task/{taskId}")
    public MessageDto createForTask(@PathVariable Long taskId, @Valid @RequestBody MessageDto dto) {
        return messageService.createForTask(taskId, dto);
    }

    @GetMapping("/task/{taskId}")
    public List<MessageDto> getByTask(@PathVariable Long taskId) {
        return messageService.findByTask(taskId);
    }

    // paginated messages for a user, optional unread filter
    @GetMapping("/user/{userId}")
    public org.springframework.data.domain.Page<MessageDto> getByUser(
            @PathVariable String userId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        return messageService.findByReceiver(userId, page, size, unreadOnly);
    }
}
