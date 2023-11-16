package edonymyeon.backend.member.repository;

import edonymyeon.backend.member.domain.Email;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.Nickname;
import edonymyeon.backend.member.domain.SocialInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Optional<Member> findByEmail(final Email email);

    Optional<Member> findBySocialInfo(final SocialInfo socialInfo);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
