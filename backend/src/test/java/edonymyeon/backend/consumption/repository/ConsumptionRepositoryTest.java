package edonymyeon.backend.consumption.repository;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.ProfileImageInfoTestSupport;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import({MemberTestSupport.class, PostTestSupport.class, ProfileImageInfoTestSupport.class})
@DataJpaTest
class ConsumptionRepositoryTest {

    private final ConsumptionRepository consumptionRepository;

    private final MemberTestSupport memberTestSupport;

    private final PostTestSupport postTestSupport;

    @Test
    void 특정_월의_소비_내역_리스트_조회가_잘_되는지_테스트() {
        final Member 회원 = memberTestSupport.builder().build();
        final List<Consumption> 육월_소비들 = new ArrayList<>(소비내역을_10개_생성한다(2023, 6, 회원));
        final List<Consumption> 칠월_소비들 = new ArrayList<>(소비내역을_10개_생성한다(2023, 7, 회원));
        final List<Consumption> 팔월_소비들 = new ArrayList<>(소비내역을_10개_생성한다(2023, 8, 회원));

        final List<Consumption> 육월_소비들_조회 = consumptionRepository.findByMemberIdAndYearAndMonth(회원.getId(), 2023, 6);
        final List<Consumption> 칠월_소비들_조회 = consumptionRepository.findByMemberIdAndYearAndMonth(회원.getId(), 2023, 7);
        final List<Consumption> 팔월_소비들_조회 = consumptionRepository.findByMemberIdAndYearAndMonth(회원.getId(), 2023, 8);

        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(육월_소비들_조회.size()).isEqualTo(10);
                    assertThat(육월_소비들_조회).containsAll(육월_소비들);

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
}
