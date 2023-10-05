package tr.unvercanunlu.calculator_scheduler.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "operations")
public class Operation implements Serializable {

    @Id
    private Integer code;

    private String name;

}
