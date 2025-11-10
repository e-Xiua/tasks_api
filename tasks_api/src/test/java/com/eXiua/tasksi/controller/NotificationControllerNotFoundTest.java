package com.eXiua.tasksi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import com.eXiua.tasksi.service.NotificationService;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerNotFoundTest {

    private MockMvc mockMvc;

    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        notificationService = mock(NotificationService.class);
        NotificationController controller = new NotificationController();
        ReflectionTestUtils.setField(controller, "notificationService", notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void markAsRead_notFound_returns404() throws Exception {
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Notification not found")).when(notificationService).markAsRead(999L);

        mockMvc.perform(put("/api/notifications/999/read")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Notification not found"));
    }
}
