package com.eXiua.tasksi.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.mapper.MessageMapper;
import com.eXiua.tasksi.model.Message;
import com.eXiua.tasksi.model.Task;
import com.eXiua.tasksi.repository.MessageRepository;
import com.eXiua.tasksi.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock TaskRepository taskRepository;
    @Mock MessageMapper messageMapper;
    @Mock NotificationService notificationService;

    @InjectMocks MessageService messageService;

    @Test
    void createForTask_sendsNotificationToReceiver() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Tarea prueba");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        MessageDto dto = new MessageDto();
        dto.receiverId = "user-123";
        dto.content = "Hola";

        Message saved = new Message();
        saved.setId(10L);
        saved.setReceiverId("user-123");
        saved.setContent("Hola");

        when(messageMapper.toEntity(any(MessageDto.class))).thenReturn(saved);
        when(messageRepository.save(any(Message.class))).thenReturn(saved);

        messageService.createForTask(taskId, dto);

        verify(notificationService).send("user-123", "Nuevo mensaje en la tarea: Tarea prueba", "TASK_MESSAGE");
    }
}
