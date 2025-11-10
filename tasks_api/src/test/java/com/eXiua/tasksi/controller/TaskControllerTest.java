package com.eXiua.tasksi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.model.TasksStatus;
import com.eXiua.tasksi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @Mock TaskService taskService;

    @BeforeEach
    void setUp() {
        TaskController controller = new TaskController();
        ReflectionTestUtils.setField(controller, "taskService", taskService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postInvalidTask_returns400() throws Exception {
        // missing required fields: title and status
        TaskDTO invalid = new TaskDTO();
        invalid.setDescription("only description");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void postValidTask_returns200() throws Exception {
        TaskDTO req = new TaskDTO();
        req.setTitle("Tarea de prueba");
        req.setStatus(TasksStatus.TODO);

        TaskDTO resp = new TaskDTO();
        resp.setId(123L);
        resp.setTitle(req.getTitle());
        resp.setStatus(req.getStatus());

    when(taskService.create(any(TaskDTO.class), org.mockito.ArgumentMatchers.nullable(String.class))).thenReturn(resp);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }
}
