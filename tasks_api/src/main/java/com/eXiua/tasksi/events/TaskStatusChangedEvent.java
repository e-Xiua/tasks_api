package com.eXiua.tasksi.events;

import com.eXiua.tasksi.model.TasksStatus;

public class TaskStatusChangedEvent {
    private final Long taskId;
    private final String responsibleId;
    private final TasksStatus newStatus;
    private final String title;

    public TaskStatusChangedEvent(Long taskId, String responsibleId, TasksStatus newStatus, String title) {
        this.taskId = taskId;
        this.responsibleId = responsibleId;
        this.newStatus = newStatus;
        this.title = title;
    }

    public Long getTaskId() { return taskId; }
    public String getResponsibleId() { return responsibleId; }
    public TasksStatus getNewStatus() { return newStatus; }
    public String getTitle() { return title; }
}
