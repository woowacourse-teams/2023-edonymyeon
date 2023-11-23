package edonymyeon.backend.consumption.application;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.membber.member.application.dto.ActiveMemberId;
import edonymyeon.backend.membber.member.application.dto.YearMonthDto;
import edonymyeon.backend.membber.member.domain.Member;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.TestMemberBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
class MemberConsumptionServiceImplTest {

    private final MemberConsumptionServiceImpl consumptionConfirmService;

    private final ConsumptionRepository consumptionRepository;

    private final TestMemberBuilder testMemberBuilder;

    private final PostTestSupport postTestSupport;

    @Test
    void 절약_확정시_게시글에_저장된_금액이_절약_금액이_된다() {
        final Member 글쓴이 = testMemberBuilder.builder().build();
        final Post 게시글 = postTestSupport.builder()
                .member(글쓴이)
                .build();

        consumptionConfirmService.confirmSaving(
                new ActiveMemberId(글쓴이.getId()),
                게시글.getId(),
                new YearMonthDto(2023, 7)
        );

        final Consumption 소비_내역 = consumptionRepository.findByPostId(게시글.getId()).get();
        assertSoftly(
                SoftAssertions -> {
                    assertThat(소비_내역).isNotNull();
                    assertThat(소비_내역.getConsumptionType()).isEqualTo(ConsumptionType.SAVING);
                    assertThat(소비_내역.getPrice()).isEqualTo(게시글.getPrice());
                    assertThat(소비_내역.getConsumptionYear()).isEqualTo(2023);
                    assertThat(소비_내역.getConsumptionMonth()).isEqualTo(7);
                });
    }

    @Test
    void 구매_확정시_입력받은_금액이_구매_금액이_된다() {
        final Member 글쓴이 = testMemberBuilder.builder().build();
        final Post 게시글 = postTestSupport.builder()
                .member(글쓴이)
                .build();

        consumptionConfirmService.confirmPurchase(
                new ActiveMemberId(글쓴이.getId()),
                게시글.getId(),
                10_000L,
                new YearMonthDto(2023, 7)
        );

        final Consumption 소비_내역 = consumptionRepository.findByPostId(게시글.getId()).get();
        assertSoftly(
                SoftAssertions -> {
                    assertThat(소비_내역).isNotNull();
                    assertThat(소비_내역.getConsumptionType()).isEqualTo(ConsumptionType.PURCHASE);
                    assertThat(소비_내역.getPrice()).isEqualTo(10_000L);
                    assertThat(소비_내역.getConsumptionYear()).isEqualTo(2023);
                    assertThat(소비_내역.getConsumptionMonth()).isEqualTo(7);
                });
    }
}
