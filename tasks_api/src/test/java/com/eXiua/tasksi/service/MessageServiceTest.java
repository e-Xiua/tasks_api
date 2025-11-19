package com.eXiua.tasksi.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.mapper.MessageMapper;
import com.eXiua.tasksi.model.Message;
import com.eXiua.tasksi.model.Task;
import com.eXiua.tasksi.repository.MessageRepository;
import com.eXiua.tasksi.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de MessageService")
public class MessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock TaskRepository taskRepository;
    @Mock MessageMapper messageMapper;
    @Mock NotificationService notificationService;

    @InjectMocks MessageService messageService;

    private Task task;
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Tarea prueba");

        message = new Message();
        message.setId(10L);
        message.setReceiverId("user-123");
        message.setContent("Hola");
        message.setTask(task);

        messageDto = new MessageDto();
        messageDto.id = 10L;
        messageDto.receiverId = "user-123";
        messageDto.content = "Hola";
    }

    @Test
    @DisplayName("CreateForTask - Debería enviar notificación al receptor")
    void createForTask_sendsNotificationToReceiver() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(messageMapper.toEntity(any(MessageDto.class))).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        MessageDto result = messageService.createForTask(1L, messageDto);

        // Assert
        assertNotNull(result);
        verify(notificationService).send("user-123", "Nuevo mensaje en la tarea: Tarea prueba", "TASK_MESSAGE");
    }

    @Test
    @DisplayName("CreateForTask - Debería lanzar excepción si tarea no existe")
    void testCreateForTask_TaskNotFound() {
        // Arrange
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
            () -> messageService.createForTask(99L, messageDto));
        verify(notificationService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("CreateForTask - No debería enviar notificación si no hay receiverId")
    void testCreateForTask_NoReceiver() {
        // Arrange
        messageDto.receiverId = null;
        message.setReceiverId(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(messageMapper.toEntity(any(MessageDto.class))).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        messageService.createForTask(1L, messageDto);

        // Assert
        verify(notificationService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("CreateForTask - Debería manejar tarea sin título")
    void testCreateForTask_TaskWithoutTitle() {
        // Arrange
        task.setTitle(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(messageMapper.toEntity(any(MessageDto.class))).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        messageService.createForTask(1L, messageDto);

        // Assert
        verify(notificationService).send("user-123", "Nuevo mensaje en la tarea: (sin título)", "TASK_MESSAGE");
    }

    @Test
    @DisplayName("FindByTask - Debería retornar mensajes ordenados por timestamp")
    void testFindByTask_Success() {
        // Arrange
        List<Message> messages = Arrays.asList(message);
        when(messageRepository.findByTaskIdOrderByTimestampAsc(1L)).thenReturn(messages);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        List<MessageDto> result = messageService.findByTask(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageRepository).findByTaskIdOrderByTimestampAsc(1L);
    }

    @Test
    @DisplayName("FindByReceiver con paginación - Debería retornar todos los mensajes")
    void testFindByReceiverPaginated_AllMessages() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message));
        when(messageRepository.findByReceiverIdOrderByTimestampDesc("user-123", pageable))
            .thenReturn(messagePage);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        Page<MessageDto> result = messageService.findByReceiver("user-123", 0, 20, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(messageRepository).findByReceiverIdOrderByTimestampDesc("user-123", pageable);
    }

    @Test
    @DisplayName("FindByReceiver con paginación - Debería retornar solo mensajes no leídos")
    void testFindByReceiverPaginated_UnreadOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message));
        when(messageRepository.findByReceiverIdAndReadFlagFalseOrderByTimestampDesc("user-123", pageable))
            .thenReturn(messagePage);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        Page<MessageDto> result = messageService.findByReceiver("user-123", 0, 20, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(messageRepository).findByReceiverIdAndReadFlagFalseOrderByTimestampDesc("user-123", pageable);
    }

    @Test
    @DisplayName("FindByReceiver sin paginación - Debería retornar lista completa")
    void testFindByReceiver_AllMessages() {
        // Arrange
        List<Message> messages = Arrays.asList(message);
        when(messageRepository.findByReceiverIdOrderByTimestampDesc("user-123")).thenReturn(messages);
        when(messageMapper.toDto(any(Message.class))).thenReturn(messageDto);

        // Act
        List<MessageDto> result = messageService.findByReceiver("user-123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageRepository).findByReceiverIdOrderByTimestampDesc("user-123");
    }

    @Test
    @DisplayName("FindByTask - Debería retornar lista vacía si no hay mensajes")
    void testFindByTask_Empty() {
        // Arrange
        when(messageRepository.findByTaskIdOrderByTimestampAsc(99L)).thenReturn(Arrays.asList());

        // Act
        List<MessageDto> result = messageService.findByTask(99L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
