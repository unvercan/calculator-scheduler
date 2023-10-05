package tr.unvercanunlu.calculator_scheduler.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tr.unvercanunlu.calculator_scheduler.entity.Calculation;

import java.util.UUID;

@Repository
public interface ICalculationRepository extends MongoRepository<Calculation, UUID> {
}
