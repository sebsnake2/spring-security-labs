package com.example.lab1.quartz.job;

import com.example.lab1.service.ScheduledTaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuartzJobBehaviorTest {

    @Test
    @DisplayName("TaskReportJob reads parameters and delegates to Spring service")
    void taskReportJob_delegatesToService() throws Exception {
        // Arrange
        ScheduledTaskService service =
                mock(ScheduledTaskService.class);

        TaskReportJob job =
                new TaskReportJob();

        job.setScheduledTaskService(service);

        JobExecutionContext context =
                mock(JobExecutionContext.class);

        JobDetail jobDetail =
                mock(JobDetail.class);

        Trigger trigger =
                mock(Trigger.class);

        JobDataMap data =
                new JobDataMap();

        data.put(
                "reportType",
                "DETAILED"
        );

        Date scheduledFireTime =
                new Date(1_000L);

        when(context.getMergedJobDataMap())
                .thenReturn(data);

        when(context.getScheduledFireTime())
                .thenReturn(scheduledFireTime);

        when(context.getJobDetail())
                .thenReturn(jobDetail);

        when(context.getTrigger())
                .thenReturn(trigger);

        when(jobDetail.getKey())
                .thenReturn(
                        JobKey.jobKey(
                                "taskReportJob",
                                "lab6-jobs"
                        )
                );

        when(trigger.getKey())
                .thenReturn(
                        TriggerKey.triggerKey(
                                "taskReportCronTrigger",
                                "lab6-triggers"
                        )
                );

        // Act
        job.executeInternal(context);

        // Assert
        verify(service).generateTaskReport(
                "lab6-jobs.taskReportJob:"
                        + "lab6-triggers.taskReportCronTrigger:"
                        + "1000",
                "DETAILED"
        );

        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Missing required JobDataMap parameter causes explicit error")
    void taskReportJob_missingParameter_throwsException() {
        // Arrange
        TaskReportJob job =
                new TaskReportJob();

        job.setScheduledTaskService(
                mock(ScheduledTaskService.class)
        );

        JobExecutionContext context =
                mock(JobExecutionContext.class);

        when(context.getMergedJobDataMap())
                .thenReturn(
                        new JobDataMap()
                );

        // Act
        JobExecutionException exception =
                assertThrows(
                        JobExecutionException.class,
                        () -> job.executeInternal(context)
                );

        // Assert
        assertEquals(
                "Required parameter 'reportType' is missing",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("TaskReportJob forbids concurrent execution")
    void taskReportJob_hasDisallowConcurrentExecution() {
        assertTrue(
                TaskReportJob.class.isAnnotationPresent(
                        DisallowConcurrentExecution.class
                )
        );
    }

    @Test
    @DisplayName("Reminder failure waits for next scheduled execution")
    void taskReminderJob_failureDoesNotRefireImmediately()
            throws Exception {

        // Arrange
        ScheduledTaskService service =
                mock(ScheduledTaskService.class);

        doThrow(
                new IllegalStateException(
                        "Simulated service failure"
                )
        ).when(service).sendTaskReminder(
                anyString(),
                eq("student@example.com")
        );

        TaskReminderJob job =
                new TaskReminderJob();

        job.setScheduledTaskService(service);

        JobExecutionContext context =
                mock(JobExecutionContext.class);

        JobDetail jobDetail =
                mock(JobDetail.class);

        Trigger trigger =
                mock(Trigger.class);

        JobDataMap data =
                new JobDataMap();

        data.put(
                "recipient",
                "student@example.com"
        );

        when(context.getMergedJobDataMap())
                .thenReturn(data);

        when(context.getScheduledFireTime())
                .thenReturn(
                        new Date(2_000L)
                );

        when(context.getJobDetail())
                .thenReturn(jobDetail);

        when(context.getTrigger())
                .thenReturn(trigger);

        when(jobDetail.getKey())
                .thenReturn(
                        JobKey.jobKey(
                                "taskReminderJob",
                                "lab6-jobs"
                        )
                );

        when(trigger.getKey())
                .thenReturn(
                        TriggerKey.triggerKey(
                                "taskReminderCronTrigger",
                                "lab6-triggers"
                        )
                );

        // Act
        JobExecutionException exception =
                assertThrows(
                        JobExecutionException.class,
                        () -> job.executeInternal(context)
                );

        // Assert
        assertEquals(
                "Task reminder failed",
                exception.getMessage()
        );

        assertFalse(
                exception.refireImmediately()
        );
    }

    @Test
    @DisplayName("Transaction boundary is defined on Spring service methods")
    void transactionBoundary_isDefinedOnService() throws Exception {
        Method reportMethod =
                ScheduledTaskService.class
                        .getMethod(
                                "generateTaskReport",
                                String.class,
                                String.class
                        );

        Method reminderMethod =
                ScheduledTaskService.class
                        .getMethod(
                                "sendTaskReminder",
                                String.class,
                                String.class
                        );

        assertTrue(
                reportMethod.isAnnotationPresent(
                        Transactional.class
                )
        );

        assertTrue(
                reminderMethod.isAnnotationPresent(
                        Transactional.class
                )
        );
    }
}