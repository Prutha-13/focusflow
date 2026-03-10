package com.focusflow.service;

import com.focusflow.dto.TaskRequestDTO;
import com.focusflow.dto.TaskResponseDTO;
import com.focusflow.entity.Task;
import com.focusflow.entity.Task.TaskStatus;
import com.focusflow.exception.TaskNotFoundException;
import com.focusflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task(
            "Write unit tests",
            "Test the service layer",
            Task.Priority.HIGH,
            2
        );
        // Simulate DB-assigned ID
        sampleTask.setId(1L);
    }

    // ── Test 1: Create Task ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should create a task and return response DTO")
    void shouldCreateTask() {
        // Arrange
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Write unit tests");
        request.setDescription("Test the service layer");
        request.setPriority(Task.Priority.HIGH);
        request.setEstimatedPomodoros(2);

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        TaskResponseDTO result = taskService.createTask(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Write unit tests");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(result.getPomodoroCount()).isEqualTo(0);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should default priority to MEDIUM when not provided")
    void shouldDefaultPriorityToMedium() {
        // Arrange
        TaskRequestDTO request = new TaskRequestDTO();
        request.setTitle("Quick task");
        // priority intentionally NOT set

        Task savedTask = new Task("Quick task", null, Task.Priority.MEDIUM, 1);
        savedTask.setId(2L);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponseDTO result = taskService.createTask(request);

        // Assert
        assertThat(result.getPriority()).isEqualTo(Task.Priority.MEDIUM);
    }

    // ── Test 2: Complete Task ─────────────────────────────────────────────────

    @Test
    @DisplayName("Should mark task as COMPLETED")
    void shouldCompleteTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        TaskResponseDTO result = taskService.completeTask(1L);

        // Assert
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository).save(sampleTask);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when completing non-existent task")
    void shouldThrowWhenCompletingMissingTask() {
        // Arrange
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.completeTask(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── Test 3: Increment Pomodoro ────────────────────────────────────────────

    @Test
    @DisplayName("Should increment pomodoro count and set status to IN_PROGRESS")
    void shouldIncrementPomodoroAndSetInProgress() {
        // Arrange
        assertThat(sampleTask.getPomodoroCount()).isEqualTo(0);
        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.PENDING);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        taskService.incrementPomodoro(1L);

        // Assert
        assertThat(sampleTask.getPomodoroCount()).isEqualTo(1);
        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should not change status to IN_PROGRESS if task is already COMPLETED")
    void shouldNotChangeStatusIfAlreadyCompleted() {
        // Arrange
        sampleTask.setStatus(TaskStatus.COMPLETED);
        sampleTask.setPomodoroCount(3);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        taskService.incrementPomodoro(1L);

        // Assert
        assertThat(sampleTask.getPomodoroCount()).isEqualTo(4);
        assertThat(sampleTask.getStatus()).isEqualTo(TaskStatus.COMPLETED); // stays COMPLETED
    }

    // ── Test 4: Delete Task ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete task successfully when it exists")
    void shouldDeleteTask() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when deleting non-existent task")
    void shouldThrowWhenDeletingMissingTask() {
        // Arrange
        when(taskRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> taskService.deleteTask(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");

        verify(taskRepository, never()).deleteById(any());
    }

    // ── Test 5: Get Active Tasks ──────────────────────────────────────────────

    @Test
    @DisplayName("Should return only PENDING and IN_PROGRESS tasks")
    void shouldReturnActiveTasks() {
        // Arrange
        Task pendingTask    = new Task("Pending Task",    null, Task.Priority.HIGH,   1);
        Task inProgressTask = new Task("In Progress",     null, Task.Priority.MEDIUM, 2);
        Task completedTask  = new Task("Completed Task",  null, Task.Priority.LOW,    1);

        pendingTask.setId(1L);
        inProgressTask.setId(2L);
        inProgressTask.setStatus(TaskStatus.IN_PROGRESS);
        completedTask.setId(3L);
        completedTask.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByStatusInOrderByPriorityDescCreatedAtAsc(
            List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS)
        )).thenReturn(List.of(pendingTask, inProgressTask));

        // Act
        List<TaskResponseDTO> result = taskService.getActiveTasks();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TaskResponseDTO::getStatus)
            .containsOnly(TaskStatus.PENDING, TaskStatus.IN_PROGRESS)
            .doesNotContain(TaskStatus.COMPLETED);
    }

    // ── Test 6: Update Task ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should update only provided fields, leaving others unchanged")
    void shouldUpdateOnlyProvidedFields() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskRequestDTO updateRequest = new TaskRequestDTO();
        updateRequest.setTitle("Updated Title");
        // description, priority NOT provided — should stay unchanged

        // Act
        taskService.updateTask(1L, updateRequest);

        // Assert
        assertThat(sampleTask.getTitle()).isEqualTo("Updated Title");
        assertThat(sampleTask.getDescription()).isEqualTo("Test the service layer"); // unchanged
        assertThat(sampleTask.getPriority()).isEqualTo(Task.Priority.HIGH);          // unchanged
    }
}