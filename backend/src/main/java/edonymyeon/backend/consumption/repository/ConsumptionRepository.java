package edonymyeon.backend.consumption.repository;

import edonymyeon.backend.consumption.domain.Consumption;
import java.time.LocalDate;
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
            + "AND c.consumptionDate BETWEEN :startDate AND :endDate")
    List<Consumption> findByMemberIdAndConsumptionDateBetween(
            @Param("memberId") final Long memberId,
            @Param("startDate") final LocalDate startDate,
            @Param("endDate") final LocalDate endDate
    );

    @Query("SELECT c FROM Consumption as c WHERE c.post.id IN (:postIds)")
    List<Consumption> findAllByPostIds(@Param("postIds") List<Long> postIds);

    void deleteByPostId(final Long postId);
}
