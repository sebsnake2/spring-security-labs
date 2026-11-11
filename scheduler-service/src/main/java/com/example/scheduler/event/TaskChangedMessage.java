package com.example.scheduler.event;

import java.time.Instant;

public record TaskChangedMessage(
        String eventId,
        String eventType,
        Long taskId,
        String title,
        String status,
        Instant occurredAt
) {
}