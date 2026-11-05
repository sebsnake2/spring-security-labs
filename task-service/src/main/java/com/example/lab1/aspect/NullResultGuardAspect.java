package com.example.lab1.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NullResultGuardAspect {

    @AfterReturning(
            pointcut = "@annotation(com.example.lab1.annotation.aop.RejectNullResult)",
            returning = "result"
    )
    public void rejectNullResult(Object result) {
        if (result == null) {
            throw new IllegalStateException(
                    "Annotated method must not return null"
            );
        }
    }
}