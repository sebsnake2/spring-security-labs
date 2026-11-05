package com.example.scheduler.quartz.job;

import com.example.scheduler.service.ScheduledTaskService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@DisallowConcurrentExecution
public class TaskReportJob extends QuartzJobBean {

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

        String reportType =
                data.getString("reportType");

        if (reportType == null || reportType.isBlank()) {
            throw new JobExecutionException(
                    "Required parameter 'reportType' is missing"
            );
        }

        String executionKey =
                createExecutionKey(context);

        scheduledTaskService.generateTaskReport(
                executionKey,
                reportType
        );
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