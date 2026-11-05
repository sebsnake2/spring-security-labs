package com.example.lab1.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class MethodSecurityService {

    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnlyMethod() {
        return "Method security access granted";
    }
}