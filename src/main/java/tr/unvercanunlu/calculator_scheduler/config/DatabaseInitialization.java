package tr.unvercanunlu.calculator_scheduler.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import tr.unvercanunlu.calculator_scheduler.entity.Operation;
import tr.unvercanunlu.calculator_scheduler.repository.IOperationRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DatabaseInitialization {

    private final IOperationRepository operationRepository;

    private final Gson gson = new Gson();

    @Value(value = "${data.initial.location.operation}")
    private String filePath;

    @SneakyThrows
    private InputStream getFileAsStream(String filePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(filePath);
    }

    @SneakyThrows
    private String readStreamByLine(InputStream stream) {
        List<String> lines = new ArrayList<>();

        InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return String.join("", lines);
    }

    @SneakyThrows
    @EventListener(value = ApplicationReadyEvent.class)
    public void initialize() {
        System.out.println("Database initializing is started.");

        System.out.println("File at '" + this.filePath + "' is reading.");

        InputStream stream = this.getFileAsStream(this.filePath);
        String content = this.readStreamByLine(stream);

        System.out.println("File at '" + this.filePath + "' is read.");

        System.out.println("File content: '" + content + "'.");

        Type typeOfListOfOperations = new TypeToken<List<Operation>>() {
        }.getType();

        List<Operation> operations = this.gson.fromJson(content, typeOfListOfOperations);

        System.out.println("'" + content + "' is converted to " + operations);

        this.operationRepository.deleteAll();

        System.out.println("All operations in the database are deleted.");

        operations.forEach(operation -> {
            this.operationRepository.save(operation);

            System.out.println(operation + " is saved to database.");
        });

        System.out.println("Database initializing is end.");
    }
}
