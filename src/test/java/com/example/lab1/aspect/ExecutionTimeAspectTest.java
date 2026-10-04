package com.example.lab1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionTimeAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private ExecutionTimeAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new ExecutionTimeAspect();
    }

    @Test
    @DisplayName("Around advice calls proceed and returns original result")
    void measureExecutionTime_callsProceedAndReturnsOriginalResult()
            throws Throwable {

        // Arrange
        when(joinPoint.proceed())
                .thenReturn("original result");

        when(joinPoint.getSignature())
                .thenReturn(signature);

        when(signature.toShortString())
                .thenReturn("TestService.testMethod()");

        // Act
        Object result =
                aspect.measureExecutionTime(joinPoint);

        // Assert
        assertEquals("original result", result);
        verify(joinPoint).proceed();
    }
}