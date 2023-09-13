package edonymyeon.backend.member.repository;

import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<Member> findAllHavingUnConfirmedPost() {
        return entityManager
                .createQuery(
                        "SELECT p.member FROM Post p LEFT OUTER JOIN Consumption c ON p.id = c.post.id WHERE c IS NULL",
                        Member.class)
                .getResultList();
    }
}
