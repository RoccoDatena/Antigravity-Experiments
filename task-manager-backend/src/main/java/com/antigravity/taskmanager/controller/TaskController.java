package com.antigravity.taskmanager.controller;

import com.antigravity.taskmanager.dto.TaskRequest;
import com.antigravity.taskmanager.dto.TaskResponse;
import com.antigravity.taskmanager.model.User;
import com.antigravity.taskmanager.repository.UserRepository;
import com.antigravity.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private final com.antigravity.taskmanager.service.AwsBedrockService awsBedrockService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(taskService.getTasks(user, status, priority, search));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TaskRequest request
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(taskService.createTask(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TaskRequest request
    ) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);
        taskService.deleteTask(id, user);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(taskService.getStats());
    }

    @GetMapping("/{id}/ai-suggest")
    public ResponseEntity<List<String>> getAiSuggestions(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = getUser(userDetails);
        // We load the task to verify ownership/access
        List<TaskResponse> userTasks = taskService.getTasks(user, null, null, null);
        TaskResponse targetTask = userTasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        List<String> suggestions = awsBedrockService.suggestSubtasks(targetTask.getTitle(), targetTask.getDescription());
        return ResponseEntity.ok(suggestions);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
