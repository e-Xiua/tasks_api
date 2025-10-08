package com.eXiua.tasksi.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.model.Task;
import com.eXiua.tasksi.model.TaskHistory;
import com.eXiua.tasksi.repository.TaskHistoryRepository;
import com.eXiua.tasksi.repository.TaskRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskHistoryRepository historyRepository;

    private TaskDTO toDTO(Task t) {
        TaskDTO d = new TaskDTO();
        BeanUtils.copyProperties(t, d);
        return d;
    }

    private Task fromDTO(TaskDTO d) {
        Task t = new Task();
        BeanUtils.copyProperties(d, t);
        return t;
    }

    @Override
    public TaskDTO create(TaskDTO dto, String actorId) {
        Task t = fromDTO(dto);
        Task saved = taskRepository.save(t);
        TaskHistory h = new TaskHistory();
        h.setTaskId(saved.getId());
        h.setAction("CREATED");
        h.setActorId(actorId);
        h.setDetails("Created task");
        historyRepository.save(h);
        return toDTO(saved);
    }

    @Override
    public TaskDTO update(Long id, TaskDTO dto, String actorId) {
        Task existing = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        // copy only allowed fields
        existing.setTitle(dto.title);
        existing.setDescription(dto.description);
        existing.setStatus(dto.status);
        existing.setPriority(dto.priority);
        existing.setResponsibleId(dto.responsibleId);
        existing.setResponsibleName(dto.responsibleName);
        existing.setProject(dto.project);
        existing.setDueDate(dto.dueDate);
        existing.setProgress(dto.progress);
        Task saved = taskRepository.save(existing);
        TaskHistory h = new TaskHistory();
        h.setTaskId(saved.getId());
        h.setAction("UPDATED");
        h.setActorId(actorId);
        h.setDetails("Updated task");
        historyRepository.save(h);
        return toDTO(saved);
    }

    @Override
    public void delete(Long id, String actorId) {
        Task t = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(t);
        TaskHistory h = new TaskHistory();
        h.setTaskId(id);
        h.setAction("DELETED");
        h.setActorId(actorId);
        h.setDetails("Deleted task");
        historyRepository.save(h);
    }

    @Override
    public TaskDTO findById(Long id) {
        return taskRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public Page<TaskDTO> search(String status, String priority, String responsibleId, String project, LocalDate dueFrom, LocalDate dueTo, Pageable pageable) {
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            if (status != null) preds.add(cb.equal(root.get("status"), status));
            if (priority != null) preds.add(cb.equal(root.get("priority"), priority));
            if (responsibleId != null) preds.add(cb.equal(root.get("responsibleId"), responsibleId));
            if (project != null) preds.add(cb.equal(root.get("project"), project));
            if (dueFrom != null) preds.add(cb.greaterThanOrEqualTo(root.get("dueDate"), dueFrom));
            if (dueTo != null) preds.add(cb.lessThanOrEqualTo(root.get("dueDate"), dueTo));
            return cb.and(preds.toArray(new Predicate[0]));
        };

        Page<Task> page = taskRepository.findAll(spec, pageable);
        List<TaskDTO> dtos = page.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public Object kpis() {
        // Ejemplo simple: contar por estado
        long total = taskRepository.count();
        long todo = taskRepository.count((root, query, cb) -> cb.equal(root.get("status"), "TODO"));
        long inprogress = taskRepository.count((root, query, cb) -> cb.equal(root.get("status"), "IN_PROGRESS"));
        long done = taskRepository.count((root, query, cb) -> cb.equal(root.get("status"), "DONE"));
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        result.put("totalTasks", total);
        result.put("todoTasks", todo);
        result.put("inProgressTasks", inprogress);
        result.put("doneTasks", done);
        return result;
    }
}
