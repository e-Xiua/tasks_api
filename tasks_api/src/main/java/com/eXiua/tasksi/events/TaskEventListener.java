package com.eXiua.tasksi.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.eXiua.tasksi.service.NotificationService;

@Component
public class TaskEventListener {

    @Autowired private NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskAssigned(TaskAssignedEvent ev) {
        if (ev.getResponsibleId() != null) {
            String msg = "Se te ha asignado la tarea: " + (ev.getTitle() == null ? "(sin título)" : ev.getTitle());
            notificationService.send(ev.getResponsibleId(), msg, "TASK_ASSIGNED");
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskStatusChanged(TaskStatusChangedEvent ev) {
        if (ev.getResponsibleId() != null) {
            String msg = "El estado de la tarea '" + (ev.getTitle() == null ? "(sin título)" : ev.getTitle()) + "' cambió a " + ev.getNewStatus();
            notificationService.send(ev.getResponsibleId(), msg, "TASK_STATUS_CHANGED");
        }
    }
}
