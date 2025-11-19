package com.eXiua.tasksi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import com.eXiua.tasksi.dto.NotificationDto;
import com.eXiua.tasksi.mapper.NotificationMapper;
import com.eXiua.tasksi.model.Notification;
import com.eXiua.tasksi.repository.NotificationRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de NotificationService")
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setRecipientId("user-1");
        notification.setMessage("Test notification");
        notification.setType("TASK_ASSIGNED");
        notification.setReadFlag(false);

        notificationDto = new NotificationDto();
        notificationDto.id = 1L;
        notificationDto.recipientId = "user-1";
        notificationDto.message = "Test notification";
        notificationDto.type = "TASK_ASSIGNED";
        notificationDto.readFlag = false;
    }

    @Test
    @DisplayName("Send - Debería crear y guardar notificación")
    void testSend_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.send("user-1", "New task assigned", "TASK_ASSIGNED");

        // Assert
        verify(notificationRepository).save(argThat(n -> 
            n.getRecipientId().equals("user-1") &&
            n.getMessage().equals("New task assigned") &&
            n.getType().equals("TASK_ASSIGNED")
        ));
    }

    @Test
    @DisplayName("GetByRecipient - Debería retornar notificaciones paginadas")
    void testGetByRecipient_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = new PageImpl<>(Arrays.asList(notification));
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc("user-1", pageable))
            .thenReturn(notificationPage);
        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDto);

        // Act
        Page<NotificationDto> result = notificationService.getByRecipient("user-1", 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(notificationRepository).findByRecipientIdOrderByCreatedAtDesc(eq("user-1"), any(Pageable.class));
    }

    @Test
    @DisplayName("GetByRecipient - Debería retornar página vacía si no hay notificaciones")
    void testGetByRecipient_Empty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<Notification> emptyPage = Page.empty(pageable);
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(eq("user-999"), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act
        Page<NotificationDto> result = notificationService.getByRecipient("user-999", 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("MarkAsRead - Debería marcar notificación como leída")
    void testMarkAsRead_Success() {
        // Arrange
        notification.setReadFlag(false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.markAsRead(1L);

        // Assert
        verify(notificationRepository).save(argThat(n -> n.isReadFlag()));
    }

    @Test
    @DisplayName("MarkAsRead - Debería lanzar excepción si notificación no existe")
    void testMarkAsRead_NotFound() {
        // Arrange
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
            () -> notificationService.markAsRead(99L));
    }

    @Test
    @DisplayName("Send - Debería manejar mensajes vacíos")
    void testSend_EmptyMessage() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.send("user-1", "", "GENERAL");

        // Assert
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Send - Debería manejar tipo de notificación personalizado")
    void testSend_CustomType() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.send("user-1", "Custom message", "CUSTOM_TYPE");

        // Assert
        verify(notificationRepository).save(argThat(n -> 
            n.getType().equals("CUSTOM_TYPE")
        ));
    }

    @Test
    @DisplayName("GetByRecipient - Debería respetar paginación personalizada")
    void testGetByRecipient_CustomPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 5, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = new PageImpl<>(Arrays.asList(notification), pageable, 10);
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(eq("user-1"), any(Pageable.class)))
            .thenReturn(page);
        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDto);

        // Act
        Page<NotificationDto> result = notificationService.getByRecipient("user-1", 1, 5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(10, result.getTotalElements());
    }
}
