package edonymyeon.backend.member.repository;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Optional<Member> findByEmail(final String email);

    Optional<Member> findByNickname(final String nickname);

    Optional<Member> findBySocialInfo(final SocialInfo socialInfo);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
