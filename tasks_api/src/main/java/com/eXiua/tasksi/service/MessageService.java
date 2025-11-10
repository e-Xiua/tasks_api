package com.eXiua.tasksi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.mapper.MessageMapper;
import com.eXiua.tasksi.model.Message;
import com.eXiua.tasksi.model.Task;
import com.eXiua.tasksi.repository.MessageRepository;
import com.eXiua.tasksi.repository.TaskRepository;

@Service
public class MessageService {

    @Autowired private MessageRepository messageRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private MessageMapper messageMapper;
    @Autowired private NotificationService notificationService;

    @Transactional
    public MessageDto createForTask(Long taskId, MessageDto dto) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        Message m = messageMapper.toEntity(dto);
        m.setTask(task);
        Message saved = messageRepository.save(m);
        // create a notification for receiver
        if (saved.getReceiverId() != null) {
            notificationService.send(saved.getReceiverId(), "Nuevo mensaje en la tarea: " + (task.getTitle()==null?"(sin título)":task.getTitle()), "TASK_MESSAGE");
        }
        return messageMapper.toDto(saved);
    }

    public List<MessageDto> findByTask(Long taskId) {
        return messageRepository.findByTaskIdOrderByTimestampAsc(taskId).stream().map(messageMapper::toDto).toList();
    }

    public org.springframework.data.domain.Page<MessageDto> findByReceiver(String receiverId, int page, int size, boolean unreadOnly) {
        org.springframework.data.domain.Pageable p = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<com.eXiua.tasksi.model.Message> pageResult;
        if (unreadOnly) {
            pageResult = messageRepository.findByReceiverIdAndReadFlagFalseOrderByTimestampDesc(receiverId, p);
        } else {
            pageResult = messageRepository.findByReceiverIdOrderByTimestampDesc(receiverId, p);
        }
        return pageResult.map(messageMapper::toDto);
    }

    public List<MessageDto> findByReceiver(String receiverId) {
        return messageRepository.findByReceiverIdOrderByTimestampDesc(receiverId).stream().map(messageMapper::toDto).toList();
    }
}
