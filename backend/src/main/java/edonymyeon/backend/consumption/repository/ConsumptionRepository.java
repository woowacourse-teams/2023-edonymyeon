package edonymyeon.backend.consumption.repository;

import edonymyeon.backend.consumption.domain.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {

}
