package com.antigravity.taskmanager.dto;

import com.antigravity.taskmanager.model.Task;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private String ownerUsername;
    private Long ownerId;
    private String assigneeUsername;
    private Long assigneeId;
}
