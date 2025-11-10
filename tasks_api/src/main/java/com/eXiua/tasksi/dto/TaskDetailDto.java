package com.eXiua.tasksi.dto;

import java.util.List;

public class TaskDetailDto extends TaskDTO {
    public List<MessageDto> messages;

    public TaskDetailDto() {}

    public List<MessageDto> getMessages() { return messages; }
    public void setMessages(List<MessageDto> messages) { this.messages = messages; }
}
