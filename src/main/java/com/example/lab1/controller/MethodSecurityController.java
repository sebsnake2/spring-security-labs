package com.example.lab1.controller;

import com.example.lab1.service.MethodSecurityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/method-security")
public class MethodSecurityController {

    private final MethodSecurityService methodSecurityService;

    public MethodSecurityController(MethodSecurityService methodSecurityService) {
        this.methodSecurityService = methodSecurityService;
    }

    @GetMapping("/test")
    public String test() {
        return methodSecurityService.adminOnlyMethod();
    }
}