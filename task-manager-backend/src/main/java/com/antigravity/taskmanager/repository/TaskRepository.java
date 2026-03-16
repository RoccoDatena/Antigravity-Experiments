package com.antigravity.taskmanager.repository;

import com.antigravity.taskmanager.model.Task;
import com.antigravity.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwner(User owner);

    List<Task> findByStatus(Task.Status status);

    List<Task> findByPriority(Task.Priority priority);

    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Task> findWithFilters(
            @Param("status") Task.Status status,
            @Param("priority") Task.Priority priority,
            @Param("search") String search
    );

    @Query("SELECT t FROM Task t WHERE t.owner = :user AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Task> findByOwnerWithFilters(
            @Param("user") User user,
            @Param("status") Task.Status status,
            @Param("priority") Task.Priority priority,
            @Param("search") String search
    );

    long countByStatus(Task.Status status);
}
