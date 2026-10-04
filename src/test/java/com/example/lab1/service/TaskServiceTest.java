package com.example.lab1.service;

import com.example.lab1.dto.TaskDto;
import com.example.lab1.entity.Task;
import com.example.lab1.mapper.TaskMapper;
import com.example.lab1.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, taskMapper);
    }

    @Test
    @DisplayName("AC1: returns task when it exists")
    void getById_returnsTaskWhenFound() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test task");
        task.setStatus("TODO");
        task.setPriority("HIGH");

        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test task");
        taskDto.setStatus("TODO");
        taskDto.setPriority("HIGH");

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        when(taskMapper.toDto(task))
                .thenReturn(taskDto);

        // Act
        TaskDto result = taskService.getById(1L);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Test task", result.getTitle());
        assertEquals("TODO", result.getStatus());
        assertEquals("HIGH", result.getPriority());

        verify(taskRepository).findById(1L);
        verify(taskMapper).toDto(task);
    }

    @Test
    @DisplayName("AC2: throws exception when task does not exist")
    void getById_throwsNotFoundWhenTaskDoesNotExist() {
        // Arrange
        when(taskRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResponseStatusException.class,
                () -> taskService.getById(999L)
        );

        verify(taskRepository).findById(999L);
    }

    @Test
    @DisplayName("AC7: saves task and verifies repository interaction")
    void create_savesAndReturnsTask() {
        // Arrange
        TaskDto inputDto = new TaskDto();
        inputDto.setTitle("New task");
        inputDto.setDescription("Unit test task");
        inputDto.setStatus("TODO");
        inputDto.setPriority("HIGH");

        Task task = new Task();
        task.setTitle("New task");
        task.setDescription("Unit test task");
        task.setStatus("TODO");
        task.setPriority("HIGH");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("New task");
        savedTask.setDescription("Unit test task");
        savedTask.setStatus("TODO");
        savedTask.setPriority("HIGH");

        TaskDto resultDto = new TaskDto();
        resultDto.setId(1L);
        resultDto.setTitle("New task");
        resultDto.setDescription("Unit test task");
        resultDto.setStatus("TODO");
        resultDto.setPriority("HIGH");

        when(taskMapper.toEntity(inputDto))
                .thenReturn(task);

        when(taskRepository.save(task))
                .thenReturn(savedTask);

        when(taskMapper.toDto(savedTask))
                .thenReturn(resultDto);

        // Act
        TaskDto result = taskService.create(inputDto);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("New task", result.getTitle());
        assertEquals("Unit test task", result.getDescription());
        assertEquals("TODO", result.getStatus());
        assertEquals("HIGH", result.getPriority());

        verify(taskMapper).toEntity(inputDto);
        verify(taskRepository).save(task);
        verify(taskMapper).toDto(savedTask);
    }

    @Test
    @DisplayName("Updates an existing task")
    void update_updatesExistingTask() {
        // Arrange
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old title");
        existingTask.setDescription("Old description");
        existingTask.setStatus("TODO");
        existingTask.setPriority("LOW");

        TaskDto updateDto = new TaskDto();
        updateDto.setTitle("Updated title");
        updateDto.setDescription("Updated description");
        updateDto.setStatus("IN_PROGRESS");
        updateDto.setPriority("HIGH");

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated title");
        updatedTask.setDescription("Updated description");
        updatedTask.setStatus("IN_PROGRESS");
        updatedTask.setPriority("HIGH");

        TaskDto resultDto = new TaskDto();
        resultDto.setId(1L);
        resultDto.setTitle("Updated title");
        resultDto.setDescription("Updated description");
        resultDto.setStatus("IN_PROGRESS");
        resultDto.setPriority("HIGH");

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existingTask));

        when(taskRepository.save(existingTask))
                .thenReturn(updatedTask);

        when(taskMapper.toDto(updatedTask))
                .thenReturn(resultDto);

        // Act
        TaskDto result = taskService.update(1L, updateDto);

        // Assert
        assertEquals("Updated title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("HIGH", result.getPriority());

        verify(taskRepository).findById(1L);
        verify(taskMapper).updateEntity(updateDto, existingTask);
        verify(taskRepository).save(existingTask);
        verify(taskMapper).toDto(updatedTask);
    }

    @Test
    @DisplayName("AC1: returns all tasks")
    void getAll_returnsAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");

        TaskDto dto1 = new TaskDto();
        dto1.setId(1L);
        dto1.setTitle("Task 1");

        TaskDto dto2 = new TaskDto();
        dto2.setId(2L);
        dto2.setTitle("Task 2");

        when(taskRepository.findAll())
                .thenReturn(List.of(task1, task2));

        when(taskMapper.toDto(task1))
                .thenReturn(dto1);

        when(taskMapper.toDto(task2))
                .thenReturn(dto2);

        // Act
        List<TaskDto> result = taskService.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());

        verify(taskRepository).findAll();
        verify(taskMapper).toDto(task1);
        verify(taskMapper).toDto(task2);
    }

    @Test
    @DisplayName("AC7: deletes existing task and verifies repository interaction")
    void delete_deletesExistingTask() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task to delete");

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));

        // Act
        taskService.delete(1L);

        // Assert
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }
}