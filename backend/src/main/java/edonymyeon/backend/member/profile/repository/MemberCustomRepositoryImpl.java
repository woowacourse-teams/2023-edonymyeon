package edonymyeon.backend.member.profile.repository;

import edonymyeon.backend.member.profile.domain.Member;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<Member> findAllHavingUnConfirmedPostWithInDays(int days) {
        return entityManager
                .createQuery("""
                                SELECT p.member FROM Post p LEFT OUTER JOIN Consumption c ON p.id = c.post.id 
                                WHERE c IS NULL AND p.createdAt >= :daysAgo
                                """,
                        Member.class)
                .setParameter("daysAgo", LocalDateTime.now().minusDays(days))
                .getResultList();
    }
}
