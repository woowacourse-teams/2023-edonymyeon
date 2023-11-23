package edonymyeon.backend.membber.member.repository;

import edonymyeon.backend.membber.member.domain.Member;
import java.util.List;

public interface MemberCustomRepository {

    List<Member> findAllHavingUnConfirmedPostWithInDays(int days);
}
