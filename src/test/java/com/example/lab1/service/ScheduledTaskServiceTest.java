package com.example.lab1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ScheduledTaskServiceTest {

    private JdbcTemplate jdbcTemplate;
    private ScheduledTaskService service;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        service = new ScheduledTaskService(jdbcTemplate);
    }

    @Test
    @DisplayName("Repeated report execution with same key is idempotent")
    void generateTaskReport_sameExecutionKey_isIdempotent() {
        // Arrange
        when(
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM tasks",
                        Long.class
                )
        ).thenReturn(7L);

        when(
                jdbcTemplate.update(
                        anyString(),
                        any(),
                        any(),
                        any(),
                        any()
                )
        ).thenReturn(1, 0);

        // Act
        boolean firstExecution =
                service.generateTaskReport(
                        "same-execution-key",
                        "SUMMARY"
                );

        boolean secondExecution =
                service.generateTaskReport(
                        "same-execution-key",
                        "SUMMARY"
                );

        // Assert
        assertTrue(firstExecution);
        assertFalse(secondExecution);

        verify(
                jdbcTemplate,
                times(2)
        ).update(
                contains(
                        "ON CONFLICT (execution_key) DO NOTHING"
                ),
                eq("same-execution-key"),
                eq("TASK_REPORT"),
                eq("SUMMARY"),
                eq("Report SUMMARY: tasks=7")
        );
    }

    @Test
    @DisplayName("Repeated reminder execution with same key is idempotent")
    void sendTaskReminder_sameExecutionKey_isIdempotent() {
        // Arrange
        when(
                jdbcTemplate.update(
                        anyString(),
                        any(),
                        any(),
                        any(),
                        any()
                )
        ).thenReturn(1, 0);

        // Act
        boolean firstExecution =
                service.sendTaskReminder(
                        "same-reminder-key",
                        "student@example.com"
                );

        boolean secondExecution =
                service.sendTaskReminder(
                        "same-reminder-key",
                        "student@example.com"
                );

        // Assert
        assertTrue(firstExecution);
        assertFalse(secondExecution);

        verify(
                jdbcTemplate,
                times(2)
        ).update(
                contains(
                        "ON CONFLICT (execution_key) DO NOTHING"
                ),
                eq("same-reminder-key"),
                eq("TASK_REMINDER"),
                eq("student@example.com"),
                eq("Reminder prepared for student@example.com")
        );
    }
}