package edonymyeon.backend.consumption.repository;

import edonymyeon.backend.consumption.domain.Consumption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {

    Optional<Consumption> findByPostId(Long postId);

    boolean existsByPostId(Long postId);
}
