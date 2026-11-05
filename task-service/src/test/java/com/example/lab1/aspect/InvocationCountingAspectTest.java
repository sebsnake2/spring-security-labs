package com.example.lab1.aspect;

import com.example.lab1.service.AopDemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvocationCountingAspectTest {

    private InvocationCountingAspect aspect;
    private AopDemoService proxy;

    @BeforeEach
    void setUp() {
        AopDemoService target = new AopDemoService();

        aspect = new InvocationCountingAspect();

        AspectJProxyFactory factory =
                new AspectJProxyFactory(target);

        factory.addAspect(aspect);

        proxy = factory.getProxy();
    }

    @Test
    @DisplayName("External call through proxy activates aspect")
    void externalCall_activatesAspect() {
        // Act
        String result = proxy.countedMethod();

        // Assert
        assertEquals("Counted method executed", result);
        assertEquals(1, aspect.getInvocationCount());
    }

    @Test
    @DisplayName("Self invocation does not activate aspect")
    void selfInvocation_doesNotActivateAspect() {
        // Act
        String result = proxy.callCountedInternally();

        // Assert
        assertEquals("Counted method executed", result);
        assertEquals(0, aspect.getInvocationCount());
    }
}