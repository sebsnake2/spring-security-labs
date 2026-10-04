package com.example.lab1.aspect;

import com.example.lab1.service.AopDemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullResultGuardAspectTest {

    private AopDemoService proxy;

    @BeforeEach
    void setUp() {
        AopDemoService target = new AopDemoService();

        AspectJProxyFactory factory =
                new AspectJProxyFactory(target);

        factory.addAspect(new NullResultGuardAspect());

        proxy = factory.getProxy();
    }

    @Test
    @DisplayName("Non-null result passes through aspect")
    void nonNullResult_passesThroughAspect() {
        // Act
        String result = proxy.guardedMethod(false);

        // Assert
        assertEquals("Valid result", result);
    }

    @Test
    @DisplayName("Null result is rejected by aspect")
    void nullResult_isRejectedByAspect() {
        // Act + Assert
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> proxy.guardedMethod(true)
                );

        assertEquals(
                "Annotated method must not return null",
                exception.getMessage()
        );
    }
}