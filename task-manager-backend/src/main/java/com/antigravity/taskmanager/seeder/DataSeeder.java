package com.antigravity.taskmanager.seeder;

import com.antigravity.taskmanager.model.Task;
import com.antigravity.taskmanager.model.User;
import com.antigravity.taskmanager.repository.TaskRepository;
import com.antigravity.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // Skip if already seeded

        // Create users
        User admin = User.builder()
                .username("admin")
                .email("admin@taskmanager.dev")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.ADMIN)
                .build();

        User user1 = User.builder()
                .username("user1")
                .email("user1@taskmanager.dev")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.USER)
                .build();

        User user2 = User.builder()
                .username("user2")
                .email("user2@taskmanager.dev")
                .password(passwordEncoder.encode("password"))
                .role(User.Role.USER)
                .build();

        admin = userRepository.save(admin);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        // Create sample tasks
        taskRepository.save(Task.builder()
                .title("Setup CI/CD Pipeline")
                .description("Configure GitHub Actions for automated build and deploy")
                .status(Task.Status.TODO)
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDate.now().plusDays(7))
                .owner(admin)
                .assignee(user1)
                .build());

        taskRepository.save(Task.builder()
                .title("Design Database Schema")
                .description("Create ERD and finalize all table relationships")
                .status(Task.Status.DONE)
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDate.now().minusDays(3))
                .owner(admin)
                .assignee(admin)
                .build());

        taskRepository.save(Task.builder()
                .title("Implement JWT Authentication")
                .description("Add Spring Security with JWT token-based auth")
                .status(Task.Status.DONE)
                .priority(Task.Priority.HIGH)
                .dueDate(LocalDate.now().minusDays(1))
                .owner(admin)
                .assignee(admin)
                .build());

        taskRepository.save(Task.builder()
                .title("Build Angular Frontend")
                .description("Create responsive UI with Glassmorphism design")
                .status(Task.Status.IN_PROGRESS)
                .priority(Task.Priority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(14))
                .owner(user1)
                .assignee(user1)
                .build());

        taskRepository.save(Task.builder()
                .title("Write Unit Tests")
                .description("Add JUnit 5 + Mockito tests for all service classes")
                .status(Task.Status.TODO)
                .priority(Task.Priority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(21))
                .owner(user2)
                .assignee(user2)
                .build());

        taskRepository.save(Task.builder()
                .title("API Documentation")
                .description("Add Swagger/OpenAPI documentation to all endpoints")
                .status(Task.Status.TODO)
                .priority(Task.Priority.LOW)
                .dueDate(LocalDate.now().plusDays(10))
                .owner(user2)
                .assignee(null)
                .build());

        log.info("=== DataSeeder: 3 users and 6 tasks created ===");
        log.info("  admin / password (ADMIN)");
        log.info("  user1 / password (USER)");
        log.info("  user2 / password (USER)");
    }
}
