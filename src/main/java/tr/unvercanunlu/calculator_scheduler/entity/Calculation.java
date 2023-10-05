package tr.unvercanunlu.calculator_scheduler.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "calculations")
public class Calculation implements Serializable {

    @Id
    private UUID id;

    private Integer first;

    private Integer second;

    private Integer operationCode;

    private Double result;

    private LocalDateTime calculatedDate;

}
