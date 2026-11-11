--liquibase formatted sql

--changeset lab8:008-processed-task-events
CREATE TABLE processed_task_events
(
    event_id      VARCHAR(100) PRIMARY KEY,
    event_type    VARCHAR(50)  NOT NULL,
    task_id       BIGINT,
    task_title    VARCHAR(255),
    task_status   VARCHAR(50),
    occurred_at   TIMESTAMP WITH TIME ZONE,
    processed_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);