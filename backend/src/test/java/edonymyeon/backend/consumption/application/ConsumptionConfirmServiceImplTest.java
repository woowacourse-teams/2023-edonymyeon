package edonymyeon.backend.consumption.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
@SpringBootTest
class ConsumptionConfirmServiceImplTest {

    private final ConsumptionConfirmServiceImpl consumptionConfirmService;

    private final ConsumptionRepository consumptionRepository;

    private final MemberTestSupport memberTestSupport;

    private final PostTestSupport postTestSupport;

    @Test
    void 절약_확정시_게시글에_저장된_금액이_절약_금액이_된다() {
        final Member 글쓴이 = memberTestSupport.builder()
                .build();
        final Post 게시글 = postTestSupport.builder()
                .member(글쓴이)
                .build();

        consumptionConfirmService.confirmSaving(
                new MemberId(글쓴이.getId()),
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
        final Member 글쓴이 = memberTestSupport.builder()
                .build();
        final Post 게시글 = postTestSupport.builder()
                .member(글쓴이)
                .build();

        consumptionConfirmService.confirmPurchase(
                new MemberId(글쓴이.getId()),
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
