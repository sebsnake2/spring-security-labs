--liquibase formatted sql

--changeset sebastian:007-scheduled-job-executions

CREATE TABLE scheduled_job_executions
(
    execution_key   VARCHAR(255) PRIMARY KEY,
    job_type        VARCHAR(80) NOT NULL,
    parameter_value VARCHAR(255),
    result_text     VARCHAR(500),
    executed_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);