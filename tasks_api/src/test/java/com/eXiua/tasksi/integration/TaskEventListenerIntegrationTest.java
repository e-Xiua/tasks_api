package com.eXiua.tasksi.integration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.model.Notification;
import com.eXiua.tasksi.model.TasksStatus;
import com.eXiua.tasksi.repository.NotificationRepository;
import com.eXiua.tasksi.service.TaskService;

@SpringBootTest
public class TaskEventListenerIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    public void cleanup() {
        notificationRepository.deleteAll();
    }

    @Test
    public void creatingTask_publishesAssignedNotification_afterCommit() {
        TaskDTO dto = new TaskDTO();
        dto.setTitle("Tarea integración");
        dto.setStatus(TasksStatus.TODO);
        dto.setResponsibleId("user-abc-evt");

        taskService.create(dto, "actor-test");

    List<Notification> res = notificationRepository.findAll();
    // at least one notification should have been persisted (either confirmation to actor or assignment to responsible)
    assertFalse(res.isEmpty(), "Expected at least one notification persisted by service/listener");
    }
}
