package com.example.lab1.quartz.job;

import com.example.lab1.service.ScheduledTaskService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

public class TaskReminderJob extends QuartzJobBean {

    private ScheduledTaskService scheduledTaskService;

    public void setScheduledTaskService(
            ScheduledTaskService scheduledTaskService
    ) {
        this.scheduledTaskService = scheduledTaskService;
    }

    @Override
    protected void executeInternal(
            JobExecutionContext context
    ) throws JobExecutionException {

        JobDataMap data =
                context.getMergedJobDataMap();

        String recipient =
                data.getString("recipient");

        if (recipient == null || recipient.isBlank()) {
            throw new JobExecutionException(
                    "Required parameter 'recipient' is missing"
            );
        }

        String executionKey =
                createExecutionKey(context);

        try {
            scheduledTaskService.sendTaskReminder(
                    executionKey,
                    recipient
            );

        } catch (RuntimeException ex) {

            /*
             * Failure strategy:
             * record the failure and wait for
             * the next scheduled execution.
             * No immediate endless retry.
             */
            throw new JobExecutionException(
                    "Task reminder failed",
                    ex,
                    false
            );
        }
    }

    private String createExecutionKey(
            JobExecutionContext context
    ) throws JobExecutionException {

        Date scheduledFireTime =
                context.getScheduledFireTime();

        if (scheduledFireTime == null) {
            throw new JobExecutionException(
                    "Scheduled fire time is missing"
            );
        }

        return context.getJobDetail().getKey()
                + ":"
                + context.getTrigger().getKey()
                + ":"
                + scheduledFireTime.getTime();
    }
}