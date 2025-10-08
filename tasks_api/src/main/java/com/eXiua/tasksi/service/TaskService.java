package com.eXiua.tasksi.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eXiua.tasksi.dto.TaskDTO;

public interface TaskService {
    TaskDTO create(TaskDTO dto, String actorId);
    TaskDTO update(Long id, TaskDTO dto, String actorId);
    void delete(Long id, String actorId);
    TaskDTO findById(Long id);
    Page<TaskDTO> search(String status, String priority, String responsibleId, String project, LocalDate dueFrom, LocalDate dueTo, Pageable pageable);
    // KPI / Summaries
    Object kpis();
}
