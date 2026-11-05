package com.example.lab1.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RecordingAsyncExceptionHandler
        implements AsyncUncaughtExceptionHandler {

    private final AtomicReference<Throwable> lastException =
            new AtomicReference<>();

    @Override
    public void handleUncaughtException(
            Throwable ex,
            Method method,
            Object... params) {

        lastException.set(ex);

        System.err.println(
                "Async error in " + method.getName()
                        + ": " + ex.getMessage()
        );
    }

    public Throwable getLastException() {
        return lastException.get();
    }

    public void clear() {
        lastException.set(null);
    }
}