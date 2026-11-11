package com.example.lab1.messaging;

import com.example.lab1.dto.TaskDto;
import com.example.lab1.entity.Task;
import com.example.lab1.event.TaskChangedEvent;
import com.example.lab1.mapper.TaskMapper;
import com.example.lab1.repository.TaskRepository;
import com.example.lab1.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskMessagingTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, taskMapper, eventPublisher);
    }

    @Test
    @DisplayName("Creating task publishes TASK_CREATED event")
    void create_publishesTaskCreatedEvent() {
        TaskDto dto = mock(TaskDto.class);
        Task task = mock(Task.class);

        when(taskMapper.toEntity(dto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(task.getId()).thenReturn(1L);
        when(task.getTitle()).thenReturn("Lab 8 task");
        when(task.getStatus()).thenReturn("PENDING");

        taskService.create(dto);

        TaskChangedEvent event = captureEvent();

        assertEquals("TASK_CREATED", event.eventType());
        assertEquals(1L, event.taskId());
        assertEquals("Lab 8 task", event.title());
        assertEquals("PENDING", event.status());
    }

    @Test
    @DisplayName("Changing status to DONE publishes TASK_COMPLETED event")
    void updateToDone_publishesTaskCompletedEvent() {
        TaskDto dto = mock(TaskDto.class);
        Task task = mock(Task.class);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(task.getStatus()).thenReturn("PENDING", "DONE", "DONE");
        when(task.getId()).thenReturn(1L);
        when(task.getTitle()).thenReturn("Lab 8 task");

        taskService.update(1L, dto);

        TaskChangedEvent event = captureEvent();

        assertEquals("TASK_COMPLETED", event.eventType());
        assertEquals(1L, event.taskId());
        assertEquals("DONE", event.status());
    }

    @Test
    @DisplayName("Updating already completed task does not publish completion again")
    void updateAlreadyDone_doesNotPublishDuplicateCompletionEvent() {
        TaskDto dto = mock(TaskDto.class);
        Task task = mock(Task.class);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(task.getStatus()).thenReturn("DONE", "DONE");

        taskService.update(1L, dto);

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deleting task publishes TASK_DELETED event")
    void delete_publishesTaskDeletedEvent() {
        Task task = mock(Task.class);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(task.getId()).thenReturn(1L);
        when(task.getTitle()).thenReturn("Lab 8 task");
        when(task.getStatus()).thenReturn("PENDING");

        taskService.delete(1L);

        TaskChangedEvent event = captureEvent();

        assertEquals("TASK_DELETED", event.eventType());
        assertEquals(1L, event.taskId());
        assertEquals("Lab 8 task", event.title());

        verify(taskRepository).delete(task);
    }

    private TaskChangedEvent captureEvent() {
        ArgumentCaptor<TaskChangedEvent> captor =
                ArgumentCaptor.forClass(TaskChangedEvent.class);

        verify(eventPublisher).publishEvent(captor.capture());

        return captor.getValue();
    }
}