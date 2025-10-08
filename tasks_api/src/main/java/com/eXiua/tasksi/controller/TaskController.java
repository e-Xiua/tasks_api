package com.eXiua.tasksi.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // temporal, acotar a dominio front en producción
public class TaskController {

    @Autowired private TaskService taskService;

    @PostMapping
    public TaskDTO create(@RequestBody TaskDTO dto, @RequestHeader(value = "X-Actor-Id", required = false) String actorId) {
        return taskService.create(dto, actorId);
    }

    @PutMapping("/{id}")
    public TaskDTO update(@PathVariable Long id, @RequestBody TaskDTO dto, @RequestHeader(value = "X-Actor-Id", required = false) String actorId) {
        return taskService.update(id, dto, actorId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader(value = "X-Actor-Id", required = false) String actorId) {
        taskService.delete(id, actorId);
    }

    @GetMapping("/{id}")
    public TaskDTO getById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @GetMapping
    public Page<TaskDTO> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String responsibleId,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return taskService.search(status, priority, responsibleId, project, dueFrom, dueTo, p);
    }

    @GetMapping("/kpis")
    public Object kpis() {
        return taskService.kpis();
    }
}
