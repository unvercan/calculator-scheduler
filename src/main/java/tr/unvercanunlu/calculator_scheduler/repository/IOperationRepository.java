package tr.unvercanunlu.calculator_scheduler.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tr.unvercanunlu.calculator_scheduler.entity.Operation;

@Repository
public interface IOperationRepository extends MongoRepository<Operation, Integer> {
}
