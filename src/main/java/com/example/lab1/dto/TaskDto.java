package com.example.lab1.dto;

import jakarta.validation.constraints.NotBlank;
import com.example.lab1.annotation.validation.ValidTaskCompletion;

import java.time.LocalDateTime;

@ValidTaskCompletion
public class TaskDto {

    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String status;

    @NotBlank
    private String priority;

    private LocalDateTime createdAt;

    public TaskDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}