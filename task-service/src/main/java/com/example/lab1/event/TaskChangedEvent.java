package com.example.lab1.event;

import java.time.Instant;

public record TaskChangedEvent(
        String eventId,
        String eventType,
        Long taskId,
        String title,
        String status,
        Instant occurredAt
) {
}