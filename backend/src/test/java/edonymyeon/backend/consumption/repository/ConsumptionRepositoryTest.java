package edonymyeon.backend.consumption.repository;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.ConsumptionTestSupport;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.ProfileImageInfoTestSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import({MemberTestSupport.class, PostTestSupport.class, ProfileImageInfoTestSupport.class,
        ConsumptionTestSupport.class})
@DataJpaTest
class ConsumptionRepositoryTest {

    @Autowired
    private final ConsumptionRepository consumptionRepository;

    @Autowired
    private final MemberTestSupport memberTestSupport;

    @Autowired
    private ConsumptionTestSupport consumptionTestSupport;

    @Autowired
    private final PostTestSupport postTestSupport;

    @Test
    void 게시글_id로_소비내역이_잘_불러와지는지() {
        final Member member = memberTestSupport.builder().build();

        final Post post1 = postTestSupport.builder().member(member).build();
        final Post post2 = postTestSupport.builder().member(member).build();
        final Post post3 = postTestSupport.builder().member(member).build();
        final Post post4 = postTestSupport.builder().member(member).build();

        final Consumption consumption1 = consumptionTestSupport.builder().post(post1).build();
        final Consumption consumption2 = consumptionTestSupport.builder().post(post2).build();
        final Consumption consumption3 = consumptionTestSupport.builder().post(post3).build();
        final Consumption consumption4 = consumptionTestSupport.builder().post(post4).build();

        final List<Long> postIds = List.of(post1.getId(), post2.getId(), post3.getId(), post4.getId(), 5L, 6L);
        final List<Consumption> consumptions = consumptionRepository.findAllByPostIds(postIds);

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(consumptions).contains(consumption1, consumption2, consumption3, consumption4);
                    softly.assertThat(consumptions.size()).isEqualTo(4);
                }
        );
    }

    @Test
    void 특정_월의_소비_내역_리스트_조회가_잘_되는지_테스트() {
        final Member 회원 = memberTestSupport.builder().build();
        final List<Consumption> 유월_소비들 = 소비내역을_10개_생성한다(2023, 6, 회원);
        final List<Consumption> 칠월_소비들 = 소비내역을_10개_생성한다(2023, 7, 회원);
        final List<Consumption> 팔월_소비들 = 소비내역을_10개_생성한다(2023, 8, 회원);

        final List<Consumption> 유월_소비들_조회 = consumptionRepository.findByMemberIdAndConsumptionDateBetween(
                회원.getId(),
                특정_달의_첫번째_날짜(2023, 6),
                특정_달의_마지막_날짜(2023, 6)
        );
        final List<Consumption> 칠월_소비들_조회 = consumptionRepository.findByMemberIdAndConsumptionDateBetween(
                회원.getId(),
                특정_달의_첫번째_날짜(2023, 7),
                특정_달의_마지막_날짜(2023, 7)
        );
        final List<Consumption> 팔월_소비들_조회 = consumptionRepository.findByMemberIdAndConsumptionDateBetween(
                회원.getId(),
                특정_달의_첫번째_날짜(2023, 8),
                특정_달의_마지막_날짜(2023, 8)
        );

        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(유월_소비들_조회.size()).isEqualTo(10);
                    assertThat(유월_소비들_조회).containsAll(유월_소비들);

                    assertThat(칠월_소비들_조회.size()).isEqualTo(10);
                    assertThat(칠월_소비들_조회).containsAll(칠월_소비들);

                    assertThat(팔월_소비들_조회.size()).isEqualTo(10);
                    assertThat(팔월_소비들_조회).containsAll(팔월_소비들);
                }
        );
    }

    private List<Consumption> 소비내역을_10개_생성한다(final int 년도, final int 달, final Member 회원) {
        List<Consumption> 소비_내역_목록 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final Post 게시글 = postTestSupport.builder().member(회원).build();
            final Consumption 소비_내역 = Consumption.of(게시글, ConsumptionType.SAVING, 게시글.getId(), 년도, 달);
            consumptionRepository.save(소비_내역);
            소비_내역_목록.add(소비_내역);
        }
        return 소비_내역_목록;
    }

    private LocalDate 특정_달의_첫번째_날짜(final int 년도, final int 달) {
        return LocalDate.of(년도, 달, 1);
    }

    private LocalDate 특정_달의_마지막_날짜(final int 년도, final int 달) {
        final int 마지막_일 = LocalDate.of(년도, 달, 1).lengthOfMonth();
        return LocalDate.of(년도, 달, 마지막_일);
    }
}
