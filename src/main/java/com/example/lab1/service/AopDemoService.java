package com.example.lab1.service;

import com.example.lab1.annotation.aop.CountInvocation;
import org.springframework.stereotype.Service;
import com.example.lab1.annotation.aop.RejectNullResult;

@Service
public class AopDemoService {

    @CountInvocation
    public String countedMethod() {
        return "Counted method executed";
    }

    public String callCountedInternally() {
        return countedMethod();
    }

    @RejectNullResult
    public String guardedMethod(boolean returnNull) {
        return returnNull ? null : "Valid result";
    }
}