package com.eXiua.tasksi.controller;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.service.MessageService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas expandidas de MessageController")
public class MessageControllerExpandedTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
        objectMapper = new ObjectMapper();

        messageDto = new MessageDto();
        messageDto.id = 1L;
        messageDto.senderId = "sender-1";
        messageDto.receiverId = "receiver-1";
        messageDto.content = "Test message";
    }

    @Test
    @DisplayName("POST /api/messages/task/{taskId} - Debería crear mensaje exitosamente")
    void testCreateForTask_Success() throws Exception {
        // Arrange
        when(messageService.createForTask(eq(1L), any(MessageDto.class))).thenReturn(messageDto);

        // Act & Assert
        mockMvc.perform(post("/api/messages/task/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value("sender-1"))
                .andExpect(jsonPath("$.receiverId").value("receiver-1"))
                .andExpect(jsonPath("$.content").value("Test message"));

        verify(messageService).createForTask(eq(1L), any(MessageDto.class));
    }

    @Test
    @DisplayName("GET /api/messages/task/{taskId} - Debería retornar mensajes de una tarea")
    void testGetByTask_Success() throws Exception {
        // Arrange
        List<MessageDto> messages = Arrays.asList(messageDto);
        when(messageService.findByTask(1L)).thenReturn(messages);

        // Act & Assert
        mockMvc.perform(get("/api/messages/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Test message"));

        verify(messageService).findByTask(1L);
    }

    @Test
    @DisplayName("GET /api/messages/task/{taskId} - Debería retornar lista vacía si no hay mensajes")
    void testGetByTask_Empty() throws Exception {
        // Arrange
        when(messageService.findByTask(99L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/messages/task/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(messageService).findByTask(99L);
    }

    @Test
    @DisplayName("GET /api/messages/user/{userId} - Debería retornar mensajes paginados del usuario")
    void testGetByUser_Success() throws Exception {
        // Arrange
        Page<MessageDto> page = new PageImpl<>(Arrays.asList(messageDto), PageRequest.of(0, 20), 1);
        when(messageService.findByReceiver(eq("user-1"), eq(0), eq(20), eq(false))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/messages/user/user-1")
                .param("page", "0")
                .param("size", "20")
                .param("unreadOnly", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].content").value("Test message"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(messageService).findByReceiver("user-1", 0, 20, false);
    }

    @Test
    @DisplayName("GET /api/messages/user/{userId} - Debería filtrar solo mensajes no leídos")
    void testGetByUser_UnreadOnly() throws Exception {
        // Arrange
        Page<MessageDto> page = new PageImpl<>(Arrays.asList(messageDto), PageRequest.of(0, 20), 1);
        when(messageService.findByReceiver(eq("user-1"), eq(0), eq(20), eq(true))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/messages/user/user-1")
                .param("unreadOnly", "true"))
                .andExpect(status().isOk());

        verify(messageService).findByReceiver("user-1", 0, 20, true);
    }



    @Test
    @DisplayName("GET /api/messages/user/{userId} - Debería manejar paginación personalizada")
    void testGetByUser_CustomPagination() throws Exception {
        // Arrange
        Page<MessageDto> page = new PageImpl<>(Arrays.asList(messageDto), PageRequest.of(2, 10), 21);
        when(messageService.findByReceiver(eq("user-1"), eq(2), eq(10), eq(false))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/messages/user/user-1")
                .param("page", "2")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(21));

        verify(messageService).findByReceiver("user-1", 2, 10, false);
    }

    @Test
    @DisplayName("GET /api/messages/task/{taskId} - Debería retornar múltiples mensajes")
    void testGetByTask_MultipleMessages() throws Exception {
        // Arrange
        MessageDto message2 = new MessageDto();
        message2.id = 2L;
        message2.senderId = "sender-2";
        message2.receiverId = "receiver-2";
        message2.content = "Second message";

        List<MessageDto> messages = Arrays.asList(messageDto, message2);
        when(messageService.findByTask(1L)).thenReturn(messages);

        // Act & Assert
        mockMvc.perform(get("/api/messages/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Test message"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].content").value("Second message"));
    }

    @Test
    @DisplayName("POST /api/messages/task/{taskId} - Debería manejar diferentes taskIds")
    void testCreateForTask_DifferentTaskIds() throws Exception {
        // Arrange
        when(messageService.createForTask(eq(5L), any(MessageDto.class))).thenReturn(messageDto);

        // Act & Assert
        mockMvc.perform(post("/api/messages/task/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk());

        verify(messageService).createForTask(eq(5L), any(MessageDto.class));
    }

    @Test
    @DisplayName("GET /api/messages/user/{userId} - Debería retornar página vacía cuando no hay mensajes")
    void testGetByUser_EmptyPage() throws Exception {
        // Arrange
        Page<MessageDto> emptyPage = Page.empty(PageRequest.of(0, 20));
        when(messageService.findByReceiver(eq("user-999"), eq(0), eq(20), eq(false))).thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/messages/user/user-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
