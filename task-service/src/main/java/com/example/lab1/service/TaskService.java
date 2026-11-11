package com.example.lab1.service;

import com.example.lab1.dto.TaskDto;
import com.example.lab1.entity.Task;
import com.example.lab1.event.TaskChangedEvent;
import com.example.lab1.mapper.TaskMapper;
import com.example.lab1.repository.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ApplicationEventPublisher eventPublisher;

    public TaskService(
            TaskRepository taskRepository,
            TaskMapper taskMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDto getById(Long id) {
        return taskMapper.toDto(findTask(id));
    }

    public TaskDto create(TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        task.setCreatedAt(LocalDateTime.now());

        Task saved = taskRepository.save(task);

        eventPublisher.publishEvent(new TaskChangedEvent(
                UUID.randomUUID().toString(),
                "TASK_CREATED",
                saved.getId(),
                saved.getTitle(),
                saved.getStatus(),
                Instant.now()
        ));

        return taskMapper.toDto(saved);
    }

    public TaskDto update(Long id, TaskDto taskDto) {
        Task task = findTask(id);
        String previousStatus = task.getStatus();

        taskMapper.updateEntity(taskDto, task);

        Task saved = taskRepository.save(task);

        if (!"DONE".equalsIgnoreCase(previousStatus)
                && "DONE".equalsIgnoreCase(saved.getStatus())) {

            eventPublisher.publishEvent(new TaskChangedEvent(
                    UUID.randomUUID().toString(),
                    "TASK_COMPLETED",
                    saved.getId(),
                    saved.getTitle(),
                    saved.getStatus(),
                    Instant.now()
            ));
        }

        return taskMapper.toDto(saved);
    }

    public void delete(Long id) {
        Task task = findTask(id);

        taskRepository.delete(task);

        eventPublisher.publishEvent(new TaskChangedEvent(
                UUID.randomUUID().toString(),
                "TASK_DELETED",
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                Instant.now()
        ));
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));
    }
}