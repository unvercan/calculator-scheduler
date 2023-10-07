package tr.unvercanunlu.calculator_scheduler.initialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import tr.unvercanunlu.calculator_scheduler.entity.Operation;
import tr.unvercanunlu.calculator_scheduler.repository.IOperationRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitialization {

    private final Logger logger = LoggerFactory.getLogger(DatabaseInitialization.class);

    private final IOperationRepository operationRepository;

    private final Gson gson = new Gson();

    @Value(value = "${data.initial.location.operation}")
    private String filePath;

    private InputStream getFileAsStream(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(filePath);
    }

    private String readStreamByLine(InputStream stream) {
        List<String> lines = new ArrayList<>();

        InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        try {
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            this.logger.info("An error is happened while reading the file.");

            throw new RuntimeException("File cannot be read.");
        }

        return String.join("", lines);
    }

    @EventListener(value = ApplicationReadyEvent.class)
    public void initialize() {
        this.logger.info("Database initializing is started.");

        this.logger.debug("File at '" + this.filePath + "' is reading.");

        InputStream stream = this.getFileAsStream(this.filePath);
        String content = this.readStreamByLine(stream);

        this.logger.debug("File at '" + this.filePath + "' is read.");

        this.logger.debug("File content: '" + content + "'.");

        Type typeOfListOfOperations = new TypeToken<List<Operation>>() {
        }.getType();

        List<Operation> operations = this.gson.fromJson(content, typeOfListOfOperations);

        this.logger.debug("'" + content + "' is converted to " + operations);

        this.operationRepository.deleteAll();

        this.logger.debug("All operations in the database are deleted.");

        operations.forEach(operation -> {
            this.operationRepository.save(operation);

            this.logger.debug(operation + " is saved to database.");
        });

        this.logger.info("Database initializing is end.");
    }
}
