package com.antigravity.taskmanager.service;

import com.antigravity.taskmanager.dto.TaskRequest;
import com.antigravity.taskmanager.dto.TaskResponse;
import com.antigravity.taskmanager.model.Task;
import com.antigravity.taskmanager.model.User;
import com.antigravity.taskmanager.repository.TaskRepository;
import com.antigravity.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<TaskResponse> getTasks(User currentUser, String statusStr, String priorityStr, String search) {
        Task.Status status = statusStr != null ? Task.Status.valueOf(statusStr) : null;
        Task.Priority priority = priorityStr != null ? Task.Priority.valueOf(priorityStr) : null;
        String searchParam = (search != null && !search.isBlank()) ? search : null;

        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        List<Task> tasks;

        if (isAdmin) {
            tasks = taskRepository.findWithFilters(status, priority, searchParam);
        } else {
            tasks = taskRepository.findByOwnerWithFilters(currentUser, status, priority, searchParam);
        }

        return tasks.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse createTask(TaskRequest request, User owner) {
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId()).orElse(null);
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.Status.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Task.Priority.MEDIUM)
                .dueDate(request.getDueDate())
                .owner(owner)
                .assignee(assignee)
                .build();

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isAdmin && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: you don't own this task");
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getAssigneeId() != null) {
            userRepository.findById(request.getAssigneeId()).ifPresent(task::setAssignee);
        }

        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));

        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isAdmin && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied: you don't own this task");
        }

        taskRepository.delete(task);
    }

    public Map<String, Long> getStats() {
        return Map.of(
                "total", taskRepository.count(),
                "todo", taskRepository.countByStatus(Task.Status.TODO),
                "inProgress", taskRepository.countByStatus(Task.Status.IN_PROGRESS),
                "done", taskRepository.countByStatus(Task.Status.DONE)
        );
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .ownerId(task.getOwner().getId())
                .ownerUsername(task.getOwner().getUsername())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeUsername(task.getAssignee() != null ? task.getAssignee().getUsername() : null)
                .build();
    }
}
