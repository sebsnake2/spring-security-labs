package com.example.lab1.async;

public record AsyncOperationResult(
        String value,
        long startedAt,
        long finishedAt,
        String threadName
) {
}