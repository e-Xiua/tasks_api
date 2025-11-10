package com.eXiua.tasksi.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public class MessageDto {
    public Long id;
    public Long taskId;
    @NotBlank
    public String senderId;
    public String receiverId;
    @NotBlank
    public String content;
    public LocalDateTime timestamp;
    public boolean readFlag;

    public MessageDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isReadFlag() { return readFlag; }
    public void setReadFlag(boolean readFlag) { this.readFlag = readFlag; }
}
