package edonymyeon.backend.member.profile.repository;

import edonymyeon.backend.member.profile.domain.Member;
import java.util.List;

public interface MemberCustomRepository {

    List<Member> findAllHavingUnConfirmedPostWithInDays(int days);
}
