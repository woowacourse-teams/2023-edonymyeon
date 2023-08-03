package edonymyeon.backend.consumption.repository;

import edonymyeon.backend.consumption.domain.Consumption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {

    Optional<Consumption> findByPostId(Long postId);

    boolean existsByPostId(Long postId);

    @Query("SELECT c FROM Consumption as c WHERE c.post.id IN (:postIds)")
    List<Consumption> findAllByPostIds(@Param("postIds") List<Long> postIds);
}
