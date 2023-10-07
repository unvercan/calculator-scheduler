package tr.unvercanunlu.calculator_scheduler.scheduler.config;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tr.unvercanunlu.calculator_scheduler.scheduler.job.CalculatorJob;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class CalculatorJobRunner {

    private final Logger logger = LoggerFactory.getLogger(CalculatorJobRunner.class);

    private final Scheduler scheduler;

    @Value(value = "#{ ${count.calculation} }")
    private Integer calculationCount;

    @Value(value = "${scheduler.cron}")
    private String cron;

    @Bean
    public void run() {
        this.logger.info("Scheduler configuration is started.");

        JobDetail randomCalculationJob = JobBuilder.newJob(CalculatorJob.class)
                .withIdentity("calculator-job-" + UUID.randomUUID())
                .usingJobData("calculationCount", this.calculationCount)
                .build();

        this.logger.debug("'" + randomCalculationJob.getKey() + "' job is created.");

        Date after3Seconds = Date.from(ZonedDateTime.now().plusSeconds(3).toInstant());

        this.cron = Optional.ofNullable(this.cron).orElse("0 * * ? * *"); // every minute as default cron

        CronExpression cronExpression;

        try {
            cronExpression = new CronExpression(this.cron);

        } catch (ParseException e) {
            this.logger.info("An error is happened while parsing the cron expression.");

            throw new RuntimeException("Cron expression cannot be parsed.");
        }

        this.logger.debug("'" + cronExpression.getCronExpression() + "' cron is created.");

        Trigger cronTriggerAfter3Second = TriggerBuilder.newTrigger()
                .withIdentity("trigger-calculator-job-" + UUID.randomUUID())
                .withSchedule(cronSchedule(cronExpression))
                .startAt(after3Seconds)
                .build();

        this.logger.debug("'" + cronTriggerAfter3Second.getKey() + "' trigger is created.");

        try {
            this.scheduler.scheduleJob(randomCalculationJob, cronTriggerAfter3Second);

        } catch (SchedulerException e) {
            this.logger.info("An error is happened while scheduling the job.");

            throw new RuntimeException("Scheduler cannot schedule the job.");
        }

        this.logger.info("Scheduler configuration is end.");
    }
}
