package com.eXiua.tasksi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.dto.TaskDetailDto;
import com.eXiua.tasksi.model.Task;
import com.eXiua.tasksi.model.TaskPriority;
import com.eXiua.tasksi.model.TasksStatus;
import com.eXiua.tasksi.repository.TaskHistoryRepository;
import com.eXiua.tasksi.repository.TaskRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de TaskServiceImpl")
public class TaskServiceImplTest {

    @Mock TaskRepository taskRepository;
    @Mock TaskHistoryRepository historyRepository;
    @Mock NotificationService notificationService;
    @Mock org.springframework.context.ApplicationEventPublisher publisher;
    @Mock com.eXiua.tasksi.mapper.TaskMapper taskMapper;
    @Mock com.eXiua.tasksi.repository.MessageRepository messageRepository;

    @InjectMocks TaskServiceImpl taskService;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TasksStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setResponsibleId("user-1");
        task.setProject("Test Project");

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus(TasksStatus.TODO);
        taskDTO.setPriority(TaskPriority.MEDIUM);
        taskDTO.setResponsibleId("user-1");
        taskDTO.setProject("Test Project");
    }

    @Test
    @DisplayName("Create - Debería crear tarea correctamente")
    void testCreate_Success() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.create(taskDTO, "actor-1");

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Create - Debería publicar evento cuando se asigna responsable")
    void testCreate_PublishesTaskAssignedEvent() {
        // Arrange
        taskDTO.setResponsibleId("user-1");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.create(taskDTO, "actor-1");

        // Assert
        verify(publisher).publishEvent(any(com.eXiua.tasksi.events.TaskAssignedEvent.class));
    }

    @Test
    @DisplayName("Create - Debería aplicar estado por defecto TODO")
    void testCreate_AppliesDefaultStatus() {
        // Arrange
        taskDTO.setStatus(null);
        Task taskWithoutStatus = new Task();
        taskWithoutStatus.setTitle("Test");
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // Act
        taskService.create(taskDTO, "actor-1");

        // Assert
        verify(taskRepository).save(argThat(t -> t.getStatus() == TasksStatus.TODO));
    }

    @Test
    @DisplayName("Update - Debería actualizar tarea existente")
    void testUpdate_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);
        taskDTO.setTitle("Updated Title");

        // Act
        TaskDTO result = taskService.update(1L, taskDTO, "actor-1");

        // Assert
        assertNotNull(result);
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Update - Debería lanzar excepción si no existe")
    void testUpdate_NotFound() {
        // Arrange
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
            () -> taskService.update(99L, taskDTO, "actor-1"));
    }

    @Test
    @DisplayName("Update - Debería publicar evento al cambiar responsable")
    void testUpdate_PublishesEventOnResponsibleChange() {
        // Arrange
        task.setResponsibleId("old-user");
        taskDTO.setResponsibleId("new-user");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.update(1L, taskDTO, "actor-1");

        // Assert
        verify(publisher).publishEvent(any(com.eXiua.tasksi.events.TaskAssignedEvent.class));
    }

    @Test
    @DisplayName("Update - Debería publicar evento al cambiar estado")
    void testUpdate_PublishesEventOnStatusChange() {
        // Arrange
        task.setStatus(TasksStatus.TODO);
        taskDTO.setStatus(TasksStatus.DONE);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.update(1L, taskDTO, "actor-1");

        // Assert
        verify(publisher).publishEvent(any(com.eXiua.tasksi.events.TaskStatusChangedEvent.class));
    }

    @Test
    @DisplayName("Delete - Debería eliminar tarea correctamente")
    void testDelete_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        taskService.delete(1L, "actor-1");

        // Assert
        verify(taskRepository).delete(task);
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Delete - Debería lanzar excepción si no existe")
    void testDelete_NotFound() {
        // Arrange
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
            () -> taskService.delete(99L, "actor-1"));
    }

    @Test
    @DisplayName("FindById - Debería retornar tarea existente")
    void testFindById_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(taskDTO.getId(), result.getId());
        verify(taskRepository).findById(1L);
    }

    @Test
    @DisplayName("FindById - Debería lanzar excepción si no existe")
    void testFindById_NotFound() {
        // Arrange
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, 
            () -> taskService.findById(99L));
    }

    @Test
    @DisplayName("Search - Debería retornar página de tareas con filtros")
    void testSearch_WithFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(Arrays.asList(task));
        when(taskRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);

        // Act
        Page<TaskDTO> result = taskService.search(
            TasksStatus.TODO, TaskPriority.MEDIUM, "user-1", "Test Project",
            LocalDate.now(), LocalDate.now().plusDays(7), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("FindAll - Debería retornar lista de tareas")
    void testFindAll_Success() {
        // Arrange
        when(taskRepository.findAll(any(Specification.class)))
            .thenReturn(Arrays.asList(task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);

        // Act
        List<TaskDTO> result = taskService.findAll(TasksStatus.TODO, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("FindDetailById - Debería retornar detalle completo de tarea")
    void testFindDetailById_Success() {
        // Arrange
        TaskDetailDto detailDto = new TaskDetailDto();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(messageRepository.findByTaskIdOrderByTimestampAsc(1L))
            .thenReturn(Collections.emptyList());
        when(taskMapper.toDetailDto(any(Task.class), anyList())).thenReturn(detailDto);

        // Act
        TaskDetailDto result = taskService.findDetailById(1L);

        // Assert
        assertNotNull(result);
        verify(taskRepository).findById(1L);
        verify(messageRepository).findByTaskIdOrderByTimestampAsc(1L);
    }

    @Test
    @DisplayName("KPIs - Debería retornar métricas calculadas")
    void testKpis_Success() {
        // Arrange
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.count(any(Specification.class))).thenReturn(3L);
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));
        when(taskRepository.findAll(any(Specification.class)))
            .thenReturn(Arrays.asList(task));

        // Act
        Object result = taskService.kpis();

        // Assert
        assertNotNull(result);
        verify(taskRepository, atLeastOnce()).count();
    }
}
