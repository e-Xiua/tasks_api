package com.eXiua.tasksi.controller;

import com.eXiua.tasksi.dto.NotificationDto;
import com.eXiua.tasksi.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas expandidas de NotificationController")
public class NotificationControllerExpandedTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();

        notificationDto = new NotificationDto();
        notificationDto.id = 1L;
        notificationDto.recipientId = "user-1";
        notificationDto.message = "New task assigned";
        notificationDto.type = "TASK_ASSIGNED";
        notificationDto.readFlag = false;
    }

    @Test
    @DisplayName("POST /api/notifications - Debería crear notificación exitosamente")
    void testCreateNotification_Success() throws Exception {
        // Arrange
        doNothing().when(notificationService).send(anyString(), anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/notifications")
                .param("recipientId", "user-1")
                .param("message", "New task assigned")
                .param("type", "TASK_ASSIGNED"))
                .andExpect(status().isOk());

        verify(notificationService).send(eq("user-1"), eq("New task assigned"), eq("TASK_ASSIGNED"));
    }

    @Test
    @DisplayName("GET /api/notifications/{recipientId} - Debería retornar notificaciones paginadas")
    void testGetNotificationsByRecipient_Success() throws Exception {
        // Arrange
        Page<NotificationDto> page = new PageImpl<>(Arrays.asList(notificationDto), PageRequest.of(0, 20), 1);
        when(notificationService.getByRecipient(eq("user-1"), eq(0), eq(20))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user-1")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].recipientId").value("user-1"))
                .andExpect(jsonPath("$.content[0].message").value("New task assigned"))
                .andExpect(jsonPath("$.content[0].type").value("TASK_ASSIGNED"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(notificationService).getByRecipient("user-1", 0, 20);
    }



    @Test
    @DisplayName("PUT /api/notifications/{id}/read - Debería marcar notificación como leída")
    void testMarkAsRead_Success() throws Exception {
        // Arrange
        doNothing().when(notificationService).markAsRead(1L);

        // Act & Assert
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1L);
    }

    @Test
    @DisplayName("GET /api/notifications/{recipientId} - Debería retornar página vacía cuando no hay notificaciones")
    void testGetNotificationsByRecipient_Empty() throws Exception {
        // Arrange
        Page<NotificationDto> emptyPage = Page.empty(PageRequest.of(0, 20));
        when(notificationService.getByRecipient(eq("user-999"), eq(0), eq(20))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/notifications/{recipientId} - Debería manejar paginación personalizada")
    void testGetNotificationsByRecipient_CustomPagination() throws Exception {
        // Arrange
        Page<NotificationDto> page = new PageImpl<>(Arrays.asList(notificationDto), PageRequest.of(1, 5), 10);
        when(notificationService.getByRecipient(eq("user-1"), eq(1), eq(5))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user-1")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5));

        verify(notificationService).getByRecipient("user-1", 1, 5);
    }

    @Test
    @DisplayName("POST /api/notifications - Debería manejar diferentes tipos de notificaciones")
    void testCreateNotification_DifferentTypes() throws Exception {
        // Arrange
        doNothing().when(notificationService).send(anyString(), anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/notifications")
                .param("recipientId", "user-1")
                .param("message", "Task status changed")
                .param("type", "STATUS_CHANGED"))
                .andExpect(status().isOk());

        verify(notificationService).send(eq("user-1"), eq("Task status changed"), eq("STATUS_CHANGED"));
    }

    @Test
    @DisplayName("GET /api/notifications/{recipientId} - Debería manejar múltiples notificaciones")
    void testGetNotificationsByRecipient_MultipleNotifications() throws Exception {
        // Arrange
        NotificationDto notification2 = new NotificationDto();
        notification2.id = 2L;
        notification2.recipientId = "user-1";
        notification2.message = "Task completed";
        notification2.type = "TASK_COMPLETED";
        notification2.readFlag = true;

        Page<NotificationDto> page = new PageImpl<>(
            Arrays.asList(notificationDto, notification2), 
            PageRequest.of(0, 20), 
            2
        );
        when(notificationService.getByRecipient(eq("user-1"), eq(0), eq(20))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].readFlag").value(false))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].readFlag").value(true))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
