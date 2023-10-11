package edonymyeon.backend.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.application.MemberConsumptionService;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class MemberRepositoryTest extends IntegrationFixture {

    private final MemberRepository memberRepository;

    @Test
    void 작성한_게시글_중_확정하지_않은_게시글이_있는_사용자를_찾아낸다(
            @Autowired MemberConsumptionService memberConsumptionService
    ) {
        final Member member = memberTestSupport.builder().build();
        final Post post1 = postTestSupport.builder().member(member).build();

        final Member member2 = memberTestSupport.builder().build();
        final Post post2 = postTestSupport.builder().member(member2).build();

        memberConsumptionService
                .confirmPurchase(new ActiveMemberId(member.getId()), post1.getId(), 4000L, new YearMonthDto(2023, 9));

        final List<Member> members = memberRepository.findAllHavingUnConfirmedPostWithInDays(31);
        assertThat(members).contains(member2);
    }

    @Test
    void 사용자들이_모두_소비확정을_했다면_빈_리스트를_반환한다(
            @Autowired MemberConsumptionService memberConsumptionService
    ) {
        final Member member = memberTestSupport.builder().build();
        final Post post1 = postTestSupport.builder().member(member).build();

        final Member member2 = memberTestSupport.builder().build();
        final Post post2 = postTestSupport.builder().member(member2).build();

        memberConsumptionService
                .confirmPurchase(new ActiveMemberId(member.getId()), post1.getId(), 4000L, new YearMonthDto(2023, 9));
        memberConsumptionService
                .confirmSaving(new ActiveMemberId(member2.getId()), post2.getId(), new YearMonthDto(2023, 9));

        final List<Member> members = memberRepository.findAllHavingUnConfirmedPostWithInDays(31);
        assertThat(members).isEmpty();
    }

    @Test
    void 절약확정을_하지않은_게시글을_여러_개_가지고_있더라도_한번만_조회한다() {
        final Member member = memberTestSupport.builder().build();
        final Post post1 = postTestSupport.builder().member(member).build();
        final Post post2 = postTestSupport.builder().member(member).build();
        final Post post3 = postTestSupport.builder().member(member).build();

        final List<Member> members = memberRepository.findAllHavingUnConfirmedPostWithInDays(31);
        assertThat(members).hasSize(1);
    }

    @Test
    void 주어진_일자을_포함해_그_이후에_쓴_게시글만_해당한다(
            @Autowired EntityManager entityManager,
            @Autowired TransactionTemplate transactionTemplate
    ) {
        final Member member = memberTestSupport.builder().build();
        transactionTemplate.executeWithoutResult(status -> entityManager.createNativeQuery("""
                        insert
                            into
                                post
                                (content, created_at, deleted, member_id, modified_at, price, title, view_count, id)
                            values
                                ('test', :daysAgo, false, :memberId, :daysAgo, 4000, 'title', 0, default)
                        """)
                .setParameter("daysAgo", LocalDateTime.now().minusDays(31).plusMinutes(1))
                .setParameter("memberId", member.getId())
                .executeUpdate());

        final List<Member> members = memberRepository.findAllHavingUnConfirmedPostWithInDays(31);
        assertThat(members).hasSize(1);
    }

    @Test
    void 주어진_일자_이전에_쓴_게시글은_포함하지_않는다(
            @Autowired EntityManager entityManager,
            @Autowired TransactionTemplate transactionTemplate
    ) {
        final Member member = memberTestSupport.builder().build();

        transactionTemplate.executeWithoutResult(status -> entityManager.createNativeQuery("""
                        insert
                            into
                                post
                                (content, created_at, deleted, member_id, modified_at, price, title, view_count, id)
                            values
                                ('test', :daysAgo, false, :memberId, :daysAgo, 4000, 'title', 0, default)
                        """)
                .setParameter("daysAgo", LocalDateTime.now().minusDays(32))
                .setParameter("memberId", member.getId())
                .executeUpdate());

        final List<Member> members = memberRepository.findAllHavingUnConfirmedPostWithInDays(31);
        assertThat(members).isEmpty();
    }
}
