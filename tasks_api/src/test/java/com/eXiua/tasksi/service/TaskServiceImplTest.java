package com.eXiua.tasksi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.model.Task;
import com.eXiua.tasksi.repository.TaskHistoryRepository;
import com.eXiua.tasksi.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock TaskRepository taskRepository;
    @Mock TaskHistoryRepository historyRepository;
    @Mock NotificationService notificationService;
    @Mock org.springframework.context.ApplicationEventPublisher publisher;
    @Mock com.eXiua.tasksi.mapper.TaskMapper taskMapper;
    @Mock com.eXiua.tasksi.repository.MessageRepository messageRepository;

    @InjectMocks TaskServiceImpl taskService;

    @Test
    void create_publishesTaskAssignedEvent_whenResponsibleSet() {
        TaskDTO dto = new TaskDTO();
        dto.setTitle("Nueva tarea");
        dto.setStatus(com.eXiua.tasksi.model.TasksStatus.TODO);
        dto.setResponsibleId("user-1");

        Task toSave = new Task();
        toSave.setTitle(dto.getTitle());
        toSave.setResponsibleId(dto.getResponsibleId());

        Task saved = new Task();
        saved.setId(100L);
        saved.setTitle(dto.getTitle());
        saved.setResponsibleId(dto.getResponsibleId());

        when(taskRepository.save(org.mockito.ArgumentMatchers.any(Task.class))).thenReturn(saved);

    taskService.create(dto, "actor-1");

    verify(publisher).publishEvent(org.mockito.ArgumentMatchers.any(com.eXiua.tasksi.events.TaskAssignedEvent.class));
    }
}
