package com.example.scheduler.service;

import com.example.scheduler.event.TaskChangedMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskEventProcessingService {

    private final JdbcTemplate jdbcTemplate;

    public TaskEventProcessingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public boolean process(TaskChangedMessage event) {
        int inserted = jdbcTemplate.update("""
                INSERT INTO processed_task_events
                    (event_id, event_type, task_id, task_title, task_status, occurred_at)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (event_id) DO NOTHING
                """,
                event.eventId(),
                event.eventType(),
                event.taskId(),
                event.title(),
                event.status(),
                java.sql.Timestamp.from(event.occurredAt())
        );

        return inserted == 1;
    }
}