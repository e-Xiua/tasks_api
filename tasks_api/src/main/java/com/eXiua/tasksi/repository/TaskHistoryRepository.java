package com.eXiua.tasksi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eXiua.tasksi.model.TaskHistory;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}

