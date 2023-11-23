package edonymyeon.backend.member.profile.repository;

import edonymyeon.backend.member.profile.domain.Nickname;
import edonymyeon.backend.member.profile.domain.SocialInfo;
import edonymyeon.backend.member.profile.domain.Email;
import edonymyeon.backend.member.profile.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    Optional<Member> findByEmail(final Email email);

    Optional<Member> findBySocialInfo(final SocialInfo socialInfo);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
