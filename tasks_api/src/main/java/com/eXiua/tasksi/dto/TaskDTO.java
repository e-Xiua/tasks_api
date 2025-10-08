package com.eXiua.tasksi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eXiua.tasksi.model.TaskPriority;
import com.eXiua.tasksi.model.TasksStatus;

public class TaskDTO {
    public Long id;
    public String title;
    public String description;
    public TasksStatus status;
    public TaskPriority priority;
    public String responsibleId;
    public String responsibleName;
    public String project;
    public Integer progress;
    public LocalDate dueDate;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    // Getters and Setters (or use Lombok for brevity)

public TaskDTO() {}

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TasksStatus getStatus() { return status; }
    public void setStatus(TasksStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public String getResponsibleId() { return responsibleId; }
    public void setResponsibleId(String responsibleId) { this.responsibleId = responsibleId; }

    public String getResponsibleName() { return responsibleName; }
    public void setResponsibleName(String responsibleName) { this.responsibleName = responsibleName; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
