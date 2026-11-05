package com.example.lab1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger log =
            LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("@annotation(com.example.lab1.annotation.aop.MeasureExecutionTime)")
    public Object measureExecutionTime(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        long start = System.nanoTime();

        Object result = joinPoint.proceed();

        long duration = System.nanoTime() - start;

        log.info(
                "Method {} executed in {} ns",
                joinPoint.getSignature().toShortString(),
                duration
        );

        return result;
    }
}