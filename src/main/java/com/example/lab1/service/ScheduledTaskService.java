package com.example.lab1.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledTaskService {

    private final JdbcTemplate jdbcTemplate;

    public ScheduledTaskService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public boolean generateTaskReport(
            String executionKey,
            String reportType
    ) {
        Long taskCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tasks",
                Long.class
        );

        int inserted = jdbcTemplate.update("""
                INSERT INTO scheduled_job_executions
                    (execution_key, job_type, parameter_value, result_text)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (execution_key) DO NOTHING
                """,
                executionKey,
                "TASK_REPORT",
                reportType,
                "Report " + reportType
                        + ": tasks=" + taskCount
        );

        return inserted == 1;
    }

    @Transactional
    public boolean sendTaskReminder(
            String executionKey,
            String recipient
    ) {
        int inserted = jdbcTemplate.update("""
                INSERT INTO scheduled_job_executions
                    (execution_key, job_type, parameter_value, result_text)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (execution_key) DO NOTHING
                """,
                executionKey,
                "TASK_REMINDER",
                recipient,
                "Reminder prepared for " + recipient
        );

        return inserted == 1;
    }

    public int countExecutions(String executionKey) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM scheduled_job_executions
                WHERE execution_key = ?
                """,
                Integer.class,
                executionKey
        );

        return count == null ? 0 : count;
    }
}