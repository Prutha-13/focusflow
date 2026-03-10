package com.focusflow.service;

import com.focusflow.dto.TaskRequestDTO;
import com.focusflow.dto.TaskResponseDTO;
import com.focusflow.entity.Task;
import com.focusflow.entity.Task.TaskStatus;
import com.focusflow.entity.Task.Priority;
import com.focusflow.exception.TaskNotFoundException;
import com.focusflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Write unit tests", "Test the service layer", Priority.HIGH, 2);
        // Simulate what the DB would assign
        try {
            var idField = Task.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(sampleTask, 1L);
        } catch (Exception ignored) {}
    }

    @Test
    void shouldCreateTaskWithGivenTitle() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Write unit tests");
        dto.setPriority(Priority.HIGH);
        dto.setEstimatedPomodoros(2);

        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.createTask(dto);

        assertThat(result.getTitle()).isEqualTo("Write unit tests");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldUseDefaultPriorityWhenNotProvided() {
        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setTitle("Quick task");

        Task defaultTask = new Task("Quick task", null, Priority.MEDIUM, 1);
        when(taskRepository.save(any(Task.class))).thenReturn(defaultTask);

        TaskResponseDTO result = taskService.createTask(dto);

        assertThat(result.getPriority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    void shouldCompleteTaskSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.completeTask(1L);

        assertThat(result).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.completeTask(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void shouldIncrementPomodoroCount() {
        sampleTask.setPomodoroCount(2);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponseDTO result = taskService.incrementPomodoro(1L);

        assertThat(result.getPomodoroCount()).isEqualTo(3);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        assertThatCode(() -> taskService.deleteTask(1L)).doesNotThrowAnyException();
        verify(taskRepository).deleteById(1L);
    }
}