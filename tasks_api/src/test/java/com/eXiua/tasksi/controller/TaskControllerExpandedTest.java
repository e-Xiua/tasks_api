package com.eXiua.tasksi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.dto.TaskDetailDto;
import com.eXiua.tasksi.model.TaskPriority;
import com.eXiua.tasksi.model.TasksStatus;
import com.eXiua.tasksi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas expandidas de TaskController")
public class TaskControllerExpandedTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus(TasksStatus.TODO);
        taskDTO.setPriority(TaskPriority.MEDIUM);
        taskDTO.setResponsibleId("user-1");
        taskDTO.setProject("Test Project");
        taskDTO.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Debería retornar tarea por ID")
    void testGetById_Success() throws Exception {
        // Arrange
        when(taskService.findById(1L)).thenReturn(taskDTO);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));

        verify(taskService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/tasks/{id}/detail - Debería retornar detalle de tarea")
    void testGetDetailById_Success() throws Exception {
        // Arrange
        TaskDetailDto detailDto = new TaskDetailDto();
        when(taskService.findDetailById(1L)).thenReturn(detailDto);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/1/detail"))
                .andExpect(status().isOk());

        verify(taskService).findDetailById(1L);
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Debería actualizar tarea")
    void testUpdate_Success() throws Exception {
        // Arrange
        taskDTO.setTitle("Updated Task");
        when(taskService.update(eq(1L), any(TaskDTO.class), anyString()))
                .thenReturn(taskDTO);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Actor-Id", "actor-1")
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));

        verify(taskService).update(eq(1L), any(TaskDTO.class), eq("actor-1"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Debería eliminar tarea")
    void testDelete_Success() throws Exception {
        // Arrange
        doNothing().when(taskService).delete(1L, "actor-1");

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/1")
                .header("X-Actor-Id", "actor-1"))
                .andExpect(status().isOk());

        verify(taskService).delete(1L, "actor-1");
    }



    @Test
    @DisplayName("GET /api/tasks/all - Debería retornar todas las tareas sin paginación")
    void testListAll_Success() throws Exception {
        // Arrange
        List<TaskDTO> tasks = Arrays.asList(taskDTO, taskDTO);
        when(taskService.findAll(any(), any(), any(), any())).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(taskService).findAll(any(), any(), any(), any());
    }

    @Test
    @DisplayName("GET /api/tasks/kpis - Debería retornar KPIs de tareas")
    void testKpis_Success() throws Exception {
        // Arrange
        Map<String, Object> kpis = new HashMap<>();
        Map<String, Object> general = new HashMap<>();
        general.put("totalTasks", 100L);
        general.put("doneTasks", 75L);
        kpis.put("general", general);
        
        when(taskService.kpis()).thenReturn(kpis);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.general.totalTasks").value(100))
                .andExpect(jsonPath("$.general.doneTasks").value(75));

        verify(taskService).kpis();
    }



    @Test
    @DisplayName("POST /api/tasks - Debería crear tarea con actor ID")
    void testCreate_WithActorId() throws Exception {
        // Arrange
        when(taskService.create(any(TaskDTO.class), eq("actor-1")))
                .thenReturn(taskDTO);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Actor-Id", "actor-1")
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(taskService).create(any(TaskDTO.class), eq("actor-1"));
    }
}
