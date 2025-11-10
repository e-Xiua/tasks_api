package com.eXiua.tasksi.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.eXiua.tasksi.dto.MessageDto;
import com.eXiua.tasksi.dto.TaskDTO;
import com.eXiua.tasksi.dto.TaskDetailDto;
import com.eXiua.tasksi.model.Task;

@Component
public class TaskMapper {

    public TaskDTO toDto(Task t) {
        if (t == null) return null;
        TaskDTO d = new TaskDTO();
        BeanUtils.copyProperties(t, d);
        return d;
    }

    public TaskDetailDto toDetailDto(Task t, List<MessageDto> messages) {
        if (t == null) return null;
        TaskDetailDto d = new TaskDetailDto();
        BeanUtils.copyProperties(t, d);
        d.setMessages(messages == null ? List.of() : messages.stream().collect(Collectors.toList()));
        return d;
    }

    public Task toEntity(TaskDTO d) {
        if (d == null) return null;
        Task t = new Task();
        BeanUtils.copyProperties(d, t);
        return t;
    }
}
