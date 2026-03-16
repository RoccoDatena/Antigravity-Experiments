package com.antigravity.taskmanager.dto;

import com.antigravity.taskmanager.model.Task;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private LocalDate dueDate;
    private Long assigneeId;
}
