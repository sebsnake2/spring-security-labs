package com.example.lab1.service;

import com.example.lab1.dto.TaskDto;
import com.example.lab1.entity.Task;
import com.example.lab1.mapper.TaskMapper;
import com.example.lab1.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
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

        return taskMapper.toDto(taskRepository.save(task));
    }

    public TaskDto update(Long id, TaskDto taskDto) {
        Task task = findTask(id);

        taskMapper.updateEntity(taskDto, task);

        return taskMapper.toDto(taskRepository.save(task));
    }

    public void delete(Long id) {
        Task task = findTask(id);
        taskRepository.delete(task);
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));
    }
}