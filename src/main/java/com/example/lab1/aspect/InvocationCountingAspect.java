package com.example.lab1.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class InvocationCountingAspect {

    private final AtomicInteger invocationCount = new AtomicInteger();

    @Before("@annotation(com.example.lab1.annotation.aop.CountInvocation)")
    public void countInvocation() {
        invocationCount.incrementAndGet();
    }

    public int getInvocationCount() {
        return invocationCount.get();
    }

    public void reset() {
        invocationCount.set(0);
    }
}