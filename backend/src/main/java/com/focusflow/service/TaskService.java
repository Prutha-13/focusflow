package com.focusflow.service;

import com.focusflow.dto.TaskRequestDTO;
import com.focusflow.dto.TaskResponseDTO;
import com.focusflow.dto.WeeklyStatsDTO;
import com.focusflow.entity.Task;
import com.focusflow.entity.Task.TaskStatus;
import com.focusflow.exception.TaskNotFoundException;
import com.focusflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = new Task(
            dto.getTitle(),
            dto.getDescription(),
            dto.getPriority() != null ? dto.getPriority() : Task.Priority.MEDIUM,
            dto.getEstimatedPomodoros() != null ? dto.getEstimatedPomodoros() : 1
        );
        return toResponseDTO(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        return toResponseDTO(findTaskOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getActiveTasks() {
        return taskRepository.findByStatusInOrderByPriorityDescCreatedAtAsc(
            List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS)
        ).stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatusOrderByPriorityDescCreatedAtAsc(status)
            .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> searchTasks(String query) {
        return taskRepository.searchTasks(query)
            .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task task = findTaskOrThrow(id);
        if (dto.getTitle() != null)              task.setTitle(dto.getTitle());
        if (dto.getDescription() != null)        task.setDescription(dto.getDescription());
        if (dto.getStatus() != null)             task.setStatus(dto.getStatus());
        if (dto.getPriority() != null)           task.setPriority(dto.getPriority());
        if (dto.getEstimatedPomodoros() != null) task.setEstimatedPomodoros(dto.getEstimatedPomodoros());
        return toResponseDTO(taskRepository.save(task));
    }

    public TaskResponseDTO completeTask(Long id) {
        Task task = findTaskOrThrow(id);
        task.setStatus(TaskStatus.COMPLETED);
        return toResponseDTO(taskRepository.save(task));
    }

    public TaskResponseDTO incrementPomodoro(Long id) {
        Task task = findTaskOrThrow(id);
        task.setPomodoroCount(task.getPomodoroCount() + 1);
        if (task.getStatus() == TaskStatus.PENDING) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        return toResponseDTO(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public TaskResponseDTO archiveTask(Long id) {
        Task task = findTaskOrThrow(id);
        task.setStatus(TaskStatus.ARCHIVED);
        return toResponseDTO(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public WeeklyStatsDTO getWeeklyStats() {
        LocalDateTime weekStart = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime weekEnd   = LocalDate.now().atTime(LocalTime.MAX);

        List<Object[]> rawStats = taskRepository.getWeeklyStats(weekStart, weekEnd);

        Map<String, WeeklyStatsDTO.DayStat> statsMap = rawStats.stream().collect(
            Collectors.toMap(
                row -> row[0].toString(),
                row -> new WeeklyStatsDTO.DayStat(
                    row[0].toString(),
                    ((Number) row[1]).intValue(),
                    ((Number) row[2]).intValue()
                )
            )
        );

        long totalCompleted  = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long totalPending    = taskRepository.countByStatus(TaskStatus.PENDING);
        long totalInProgress = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);

        return new WeeklyStatsDTO(statsMap, totalCompleted, totalPending, totalInProgress);
    }

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    private TaskResponseDTO toResponseDTO(Task task) {
        return new TaskResponseDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getPomodoroCount(),
            task.getEstimatedPomodoros(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getCompletedAt()
        );
    }
}