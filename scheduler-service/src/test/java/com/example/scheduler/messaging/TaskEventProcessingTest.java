package com.example.scheduler.messaging;

import com.example.scheduler.config.RabbitConfig;
import com.example.scheduler.event.TaskChangedMessage;
import com.example.scheduler.event.TaskEventConsumer;
import com.example.scheduler.service.TaskEventProcessingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TaskEventProcessingTest {

    @Test
    @DisplayName("New event is processed successfully")
    void newEventIsProcessed() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        when(jdbcTemplate.update(
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(1);

        TaskEventProcessingService service =
                new TaskEventProcessingService(jdbcTemplate);

        boolean processed = service.process(createEvent());

        assertTrue(processed);
    }

    @Test
    @DisplayName("Duplicate event is ignored")
    void duplicateEventIsIgnored() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        when(jdbcTemplate.update(
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(0);

        TaskEventProcessingService service =
                new TaskEventProcessingService(jdbcTemplate);

        boolean processed = service.process(createEvent());

        assertFalse(processed);
    }

    @Test
    @DisplayName("Database insert uses ON CONFLICT for idempotency")
    void usesDatabaseConflictProtection() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        when(jdbcTemplate.update(
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(1);

        TaskEventProcessingService service =
                new TaskEventProcessingService(jdbcTemplate);

        service.process(createEvent());

        verify(jdbcTemplate).update(
                argThat(sql -> sql.contains("ON CONFLICT")
                        && sql.contains("DO NOTHING")),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("Consumer listens to durable task events queue")
    void consumerListensToTaskQueue() throws NoSuchMethodException {
        Method method = TaskEventConsumer.class
                .getDeclaredMethod("consume", String.class);

        RabbitListener annotation = method.getAnnotation(RabbitListener.class);

        assertNotNull(annotation);
        assertArrayEquals(
                new String[]{RabbitConfig.TASK_QUEUE},
                annotation.queues()
        );
    }

    private TaskChangedMessage createEvent() {
        return new TaskChangedMessage(
                "event-123",
                "TASK_CREATED",
                1L,
                "Lab 8 task",
                "PENDING",
                Instant.parse("2026-11-11T12:00:00Z")
        );
    }
}