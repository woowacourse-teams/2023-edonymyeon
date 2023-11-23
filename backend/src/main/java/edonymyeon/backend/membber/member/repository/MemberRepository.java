package edonymyeon.backend.membber.member.repository;

import edonymyeon.backend.membber.member.domain.Nickname;
import edonymyeon.backend.membber.member.domain.SocialInfo;
import edonymyeon.backend.membber.member.domain.Email;
import edonymyeon.backend.membber.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Optional<Member> findByEmail(final Email email);

    Optional<Member> findBySocialInfo(final SocialInfo socialInfo);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
