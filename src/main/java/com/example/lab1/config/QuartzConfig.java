package com.example.lab1.config;

import com.example.lab1.quartz.job.TaskReminderJob;
import com.example.lab1.quartz.job.TaskReportJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.time.ZoneId;
import java.util.TimeZone;


@Configuration
public class QuartzConfig {

    public static final String JOB_GROUP = "lab6-jobs";
    public static final String TRIGGER_GROUP = "lab6-triggers";

    public static final String TASK_REPORT_JOB = "taskReportJob";
    public static final String TASK_REMINDER_JOB = "taskReminderJob";

    @Bean
    public SchedulerFactoryBeanCustomizer quartzJobFactoryCustomizer(
            AutowireCapableBeanFactory beanFactory
    ) {
        return schedulerFactoryBean ->
                schedulerFactoryBean.setJobFactory(
                        new AutowiringSpringBeanJobFactory(
                                beanFactory
                        )
                );
    }

    @Bean
    public JobDetail taskReportJobDetail() {
        return JobBuilder
                .newJob(TaskReportJob.class)
                .withIdentity(
                        TASK_REPORT_JOB,
                        JOB_GROUP
                )
                .withDescription(
                        "Generates task reports"
                )
                .usingJobData(
                        "reportType",
                        "SUMMARY"
                )
                .requestRecovery(true)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail taskReminderJobDetail() {
        return JobBuilder
                .newJob(TaskReminderJob.class)
                .withIdentity(
                        TASK_REMINDER_JOB,
                        JOB_GROUP
                )
                .withDescription(
                        "Sends task reminders"
                )
                .usingJobData(
                        "recipient",
                        "default@example.com"
                )
                .requestRecovery(true)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger taskReportCronTrigger(
            JobDetail taskReportJobDetail
    ) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity(
                        "taskReportCronTrigger",
                        TRIGGER_GROUP
                )
                .forJob(
                        taskReportJobDetail.getKey()
                )
                .usingJobData(
                        "reportType",
                        "DETAILED"
                )
                .withSchedule(
                        CronScheduleBuilder
                                .cronSchedule(
                                        "0 0/5 * * * ?"
                                )
                                .inTimeZone(
                                        TimeZone.getTimeZone(
                                                ZoneId.of(
                                                        "Europe/Kyiv"
                                                )
                                        )
                                )
                                .withMisfireHandlingInstructionDoNothing()
                )
                .build();
    }

    @Bean
    public Trigger taskReportSimpleTrigger(
            JobDetail taskReportJobDetail
    ) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity(
                        "taskReportSimpleTrigger",
                        TRIGGER_GROUP
                )
                .forJob(
                        taskReportJobDetail.getKey()
                )
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInMinutes(10)
                                .repeatForever()
                                .withMisfireHandlingInstructionNextWithRemainingCount()
                )
                .build();
    }

    @Bean
    public Trigger taskReminderCronTrigger(
            JobDetail taskReminderJobDetail
    ) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity(
                        "taskReminderCronTrigger",
                        TRIGGER_GROUP
                )
                .forJob(
                        taskReminderJobDetail.getKey()
                )
                .usingJobData(
                        "recipient",
                        "student@example.com"
                )
                .withSchedule(
                        CronScheduleBuilder
                                .cronSchedule(
                                        "0 0 9 * * ?"
                                )
                                .inTimeZone(
                                        TimeZone.getTimeZone(
                                                ZoneId.of(
                                                        "Europe/Kyiv"
                                                )
                                        )
                                )
                                .withMisfireHandlingInstructionFireAndProceed()
                )
                .build();
    }

    private static class AutowiringSpringBeanJobFactory
            extends SpringBeanJobFactory {

        private final AutowireCapableBeanFactory beanFactory;

        private AutowiringSpringBeanJobFactory(
                AutowireCapableBeanFactory beanFactory
        ) {
            this.beanFactory = beanFactory;
        }

        @Override
        protected Object createJobInstance(
                TriggerFiredBundle bundle
        ) throws Exception {

            Object job =
                    super.createJobInstance(bundle);

            beanFactory.autowireBeanProperties(
                    job,
                    AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,
                    false
            );

            return job;
        }
    }
}