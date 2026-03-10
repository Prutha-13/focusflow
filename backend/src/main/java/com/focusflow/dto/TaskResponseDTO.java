package com.focusflow.dto;

import com.focusflow.entity.Task.Priority;
import com.focusflow.entity.Task.TaskStatus;
import java.time.LocalDateTime;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Integer pomodoroCount;
    private Integer estimatedPomodoros;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public TaskResponseDTO(Long id, String title, String description, TaskStatus status,
                           Priority priority, Integer pomodoroCount, Integer estimatedPomodoros,
                           LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime completedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.pomodoroCount = pomodoroCount;
        this.estimatedPomodoros = estimatedPomodoros;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public Integer getPomodoroCount() { return pomodoroCount; }
    public Integer getEstimatedPomodoros() { return estimatedPomodoros; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}