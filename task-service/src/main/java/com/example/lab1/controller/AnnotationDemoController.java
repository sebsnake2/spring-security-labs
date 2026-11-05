package com.example.lab1.controller;

import com.example.lab1.annotation.web.TaskGetEndpoint;
import org.springframework.web.bind.annotation.RestController;
import com.example.lab1.annotation.mvc.CurrentUsername;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.lab1.annotation.aop.MeasureExecutionTime;

@RestController
public class AnnotationDemoController {

    @TaskGetEndpoint
    public String getTaskInfo() {
        return "Task annotation endpoint works";
    }

    @GetMapping("/annotation/current-user")
    public String getCurrentUser(
            @CurrentUsername String username
    ) {
        return "Current user: " + username;
    }

    @GetMapping("/annotation/measured")
    @MeasureExecutionTime
    public String measuredEndpoint() {
        return "Measured method executed";
    }
}