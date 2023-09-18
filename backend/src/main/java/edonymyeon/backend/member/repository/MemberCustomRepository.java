package edonymyeon.backend.member.repository;

import edonymyeon.backend.member.domain.Member;
import java.util.List;

public interface MemberCustomRepository {

    List<Member> findAllHavingUnConfirmedPostWithInDays(int days);
}
