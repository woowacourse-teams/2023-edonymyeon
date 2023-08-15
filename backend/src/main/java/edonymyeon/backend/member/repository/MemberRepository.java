package edonymyeon.backend.member.repository;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.Member.SocialType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(final String email);

    Optional<Member> findByNickname(final String nickname);

    Optional<Member> findBySocialIdAndSocialType(final Long socialId, final SocialType socialType);
}
