package tr.unvercanunlu.calculator_scheduler.scheduler.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tr.unvercanunlu.calculator_scheduler.entity.Calculation;
import tr.unvercanunlu.calculator_scheduler.entity.Operation;
import tr.unvercanunlu.calculator_scheduler.repository.ICalculationRepository;
import tr.unvercanunlu.calculator_scheduler.repository.IOperationRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CalculatorJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(CalculatorJob.class);

    private static final List<Operation> operationCache = new ArrayList<>();

    private final IOperationRepository operationRepository;

    private final ICalculationRepository calculationRepository;

    private final Random random = new Random();

    @Value(value = "#{ ${gap.start} }")
    private Integer startGap;

    @Value(value = "#{ ${gap.end} }")
    private Integer endGap;

    @Override
    public void execute(JobExecutionContext context) {
        this.logger.info("Calculator job is started.");

        if (operationCache.isEmpty()) {
            this.logger.debug("Operation cache is empty.");

            List<Operation> operations = this.operationRepository.findAll();

            this.logger.debug("All operations in the database are fetched.");

            operationCache.addAll(operations);

            this.logger.debug("Operation cache is filled.");
        }

        int calculationCount = (int) Optional.ofNullable(
                context.getJobDetail().getJobDataMap().get("calculationCount")
        ).orElse(1);

        IntStream.range(0, calculationCount).forEach(i -> {
            Collections.shuffle(operationCache);

            int first = this.startGap + this.random.nextInt(this.endGap);
            int second = this.startGap + this.random.nextInt(this.endGap);

            Operation operation = operationCache.get(this.random.nextInt(operationCache.size()));

            Double result = switch (operation.getCode()) {
                case 0 -> (double) first + second;
                case 1 -> (double) first - second;
                case 2 -> (double) first * second;
                case 3 -> (double) first / second;
                case 4 -> (double) first % second;
                case 5 -> Math.pow(first, second);
                case 6 -> (double) (first + second) / 2;
                case 7 -> (double) Math.max(first, second);
                case 8 -> (double) Math.min(first, second);
                default -> null;
            };

            LocalDateTime calculatedDate = LocalDateTime.now();

            Calculation calculation = Calculation.builder()
                    .id(UUID.randomUUID())
                    .first(first)
                    .second(second)
                    .operationCode(operation.getCode())
                    .result(result)
                    .calculatedDate(calculatedDate)
                    .build();

            this.logger.debug(calculation + " is created.");

            calculation = this.calculationRepository.save(calculation);

            this.logger.debug(calculation + " is saved to database.");
        });

        this.logger.info("Calculator job is end.");
    }
}
