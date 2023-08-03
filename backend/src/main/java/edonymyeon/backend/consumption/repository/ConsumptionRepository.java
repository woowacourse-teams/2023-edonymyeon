package edonymyeon.backend.consumption.repository;

import edonymyeon.backend.consumption.domain.Consumption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {

    Optional<Consumption> findByPostId(final Long postId);

    boolean existsByPostId(final Long postId);

    @EntityGraph(attributePaths = {"post"})
    @Query("SELECT c FROM Consumption c "
            + "WHERE c.post.member.id = :memberId "
            + "AND c.consumptionYear =:consumptionYear "
            + "AND c.consumptionMonth =:consumptionMonth")
    List<Consumption> findByMemberIdAndYearAndMonth(
            @Param("memberId") final Long memberId,
            @Param("consumptionYear") final Integer consumptionYear,
            @Param("consumptionMonth") final Integer consumptionMonth
    );
}
