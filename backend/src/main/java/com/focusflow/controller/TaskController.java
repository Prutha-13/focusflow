package com.focusflow.controller;

import com.focusflow.dto.TaskRequestDTO;
import com.focusflow.dto.TaskResponseDTO;
import com.focusflow.dto.WeeklyStatsDTO;
import com.focusflow.entity.Task.TaskStatus;
import com.focusflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5500", "http://127.0.0.1:5500",  "https://prutha-13.github.io"})
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getTasks(
        @RequestParam(required = false) TaskStatus status,
        @RequestParam(required = false) String search,
        @RequestParam(required = false, defaultValue = "false") boolean activeOnly
    ) {
        List<TaskResponseDTO> tasks;
        if (search != null && !search.isBlank()) {
            tasks = taskService.searchTasks(search);
        } else if (status != null) {
            tasks = taskService.getTasksByStatus(status);
        } else if (activeOnly) {
            tasks = taskService.getActiveTasks();
        } else {
            tasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
        @PathVariable Long id,
        @Valid @RequestBody TaskRequestDTO dto
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDTO> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.completeTask(id));
    }

    @PatchMapping("/{id}/pomodoro")
    public ResponseEntity<TaskResponseDTO> incrementPomodoro(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.incrementPomodoro(id));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<TaskResponseDTO> archiveTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.archiveTask(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully", "id", id.toString()));
    }

    @GetMapping("/stats/weekly")
    public ResponseEntity<WeeklyStatsDTO> getWeeklyStats() {
        return ResponseEntity.ok(taskService.getWeeklyStats());
    }
}