package com.example.lab1.controller;

import com.example.lab1.dto.TaskDto;
import com.example.lab1.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskUiController {

    private final TaskService taskService;

    public TaskUiController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getTasks(Model model) {
        model.addAttribute("tasks", taskService.getAll());
        model.addAttribute("task", new TaskDto());

        return "tasks";
    }

    @PostMapping
    public String createTask(
            @Valid @ModelAttribute("task") TaskDto taskDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tasks", taskService.getAll());
            return "tasks";
        }

        taskService.create(taskDto);

        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editTask(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getById(id));

        return "task-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(
            @PathVariable Long id,
            @Valid @ModelAttribute("task") TaskDto taskDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "task-edit";
        }

        taskService.update(id, taskDto);

        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.delete(id);

        return "redirect:/tasks";
    }
}