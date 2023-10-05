package tr.unvercanunlu.calculator_scheduler.scheduler.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tr.unvercanunlu.calculator_scheduler.scheduler.job.CalculatorJob;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@RequiredArgsConstructor
public class CalculatorJobRunner {

    private final Scheduler scheduler;

    @Value(value = "#{ ${count.calculation} }")
    private Integer calculationCount;

    @Value(value = "${scheduler.cron}")
    private String cron;

    @SneakyThrows
    @Bean
    public void run() {
        System.out.println("Scheduler configuration is started.");

        JobDetail randomCalculationJob = JobBuilder.newJob(CalculatorJob.class)
                .withIdentity("calculator-job-" + UUID.randomUUID())
                .usingJobData("calculationCount", this.calculationCount)
                .build();

        System.out.println("'" + randomCalculationJob.getKey() + "' job is created.");

        Date after3Seconds = Date.from(ZonedDateTime.now().plusSeconds(3).toInstant());

        this.cron = Optional.ofNullable(this.cron).orElse("0 * * ? * *"); // every minute as default cron

        CronExpression cronExpression = new CronExpression(this.cron);

        System.out.println("'" + cronExpression.getCronExpression() + "' cron is created.");

        Trigger cronTriggerAfter3Second = TriggerBuilder.newTrigger()
                .withIdentity("trigger-calculator-job-" + UUID.randomUUID())
                .withSchedule(cronSchedule(cronExpression))
                .startAt(after3Seconds)
                .build();

        System.out.println("'" + cronTriggerAfter3Second.getKey() + "' trigger is created.");

        this.scheduler.scheduleJob(randomCalculationJob, cronTriggerAfter3Second);

        System.out.println("Scheduler configuration is end.");
    }
}
