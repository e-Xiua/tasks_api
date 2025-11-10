package com.eXiua.tasksi.events;

public class TaskAssignedEvent {
    private final Long taskId;
    private final String responsibleId;
    private final String title;

    public TaskAssignedEvent(Long taskId, String responsibleId, String title) {
        this.taskId = taskId;
        this.responsibleId = responsibleId;
        this.title = title;
    }

    public Long getTaskId() { return taskId; }
    public String getResponsibleId() { return responsibleId; }
    public String getTitle() { return title; }
}
