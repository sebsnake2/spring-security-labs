package com.example.scheduler.quartz;

import com.example.scheduler.config.QuartzConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuartzConfigTest {

    private QuartzConfig config;

    @BeforeEach
    void setUp() {
        config = new QuartzConfig();
    }

    @Test
    @DisplayName("Jobs and triggers have explicit names, groups and correct JobKeys")
    void jobsAndTriggers_haveCorrectIdentitiesAndLinks() {
        JobDetail reportJob =
                config.taskReportJobDetail();

        JobDetail reminderJob =
                config.taskReminderJobDetail();

        Trigger reportCron =
                config.taskReportCronTrigger(reportJob);

        Trigger reportSimple =
                config.taskReportSimpleTrigger(reportJob);

        Trigger reminderCron =
                config.taskReminderCronTrigger(reminderJob);

        assertEquals(
                JobKey.jobKey(
                        "taskReportJob",
                        "lab6-jobs"
                ),
                reportJob.getKey()
        );

        assertEquals(
                JobKey.jobKey(
                        "taskReminderJob",
                        "lab6-jobs"
                ),
                reminderJob.getKey()
        );

        assertEquals(
                reportJob.getKey(),
                reportCron.getJobKey()
        );

        assertEquals(
                reportJob.getKey(),
                reportSimple.getJobKey()
        );

        assertEquals(
                reminderJob.getKey(),
                reminderCron.getJobKey()
        );

        assertEquals(
                TriggerKey.triggerKey(
                        "taskReportCronTrigger",
                        "lab6-triggers"
                ),
                reportCron.getKey()
        );

        assertEquals(
                TriggerKey.triggerKey(
                        "taskReportSimpleTrigger",
                        "lab6-triggers"
                ),
                reportSimple.getKey()
        );

        assertEquals(
                TriggerKey.triggerKey(
                        "taskReminderCronTrigger",
                        "lab6-triggers"
                ),
                reminderCron.getKey()
        );
    }

    @Test
    @DisplayName("Trigger parameters override JobDetail default values")
    void triggerParameters_overrideJobDefaults() {
        JobDetail reportJob =
                config.taskReportJobDetail();

        Trigger reportCron =
                config.taskReportCronTrigger(reportJob);

        Trigger reportSimple =
                config.taskReportSimpleTrigger(reportJob);

        JobDataMap cronMergedData =
                new JobDataMap(
                        reportJob.getJobDataMap()
                );

        cronMergedData.putAll(
                reportCron.getJobDataMap()
        );

        assertEquals(
                "DETAILED",
                cronMergedData.getString(
                        "reportType"
                )
        );

        JobDataMap simpleMergedData =
                new JobDataMap(
                        reportJob.getJobDataMap()
                );

        simpleMergedData.putAll(
                reportSimple.getJobDataMap()
        );

        assertEquals(
                "SUMMARY",
                simpleMergedData.getString(
                        "reportType"
                )
        );
    }

    @Test
    @DisplayName("Every trigger has an explicit misfire instruction")
    void triggers_haveExplicitMisfireInstructions() {
        JobDetail reportJob =
                config.taskReportJobDetail();

        JobDetail reminderJob =
                config.taskReminderJobDetail();

        CronTrigger reportCron =
                (CronTrigger)
                        config.taskReportCronTrigger(
                                reportJob
                        );

        SimpleTrigger reportSimple =
                (SimpleTrigger)
                        config.taskReportSimpleTrigger(
                                reportJob
                        );

        CronTrigger reminderCron =
                (CronTrigger)
                        config.taskReminderCronTrigger(
                                reminderJob
                        );

        assertEquals(
                CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING,
                reportCron.getMisfireInstruction()
        );

        assertEquals(
                SimpleTrigger
                        .MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT,
                reportSimple.getMisfireInstruction()
        );

        assertEquals(
                CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW,
                reminderCron.getMisfireInstruction()
        );
    }

    @Test
    @DisplayName("Cron triggers use explicit Europe/Kyiv timezone")
    void cronTriggers_useExplicitTimezone() {
        JobDetail reportJob =
                config.taskReportJobDetail();

        JobDetail reminderJob =
                config.taskReminderJobDetail();

        CronTrigger reportCron =
                (CronTrigger)
                        config.taskReportCronTrigger(
                                reportJob
                        );

        CronTrigger reminderCron =
                (CronTrigger)
                        config.taskReminderCronTrigger(
                                reminderJob
                        );

        assertEquals(
                "Europe/Kyiv",
                reportCron.getTimeZone().getID()
        );

        assertEquals(
                "Europe/Kyiv",
                reminderCron.getTimeZone().getID()
        );
    }
}