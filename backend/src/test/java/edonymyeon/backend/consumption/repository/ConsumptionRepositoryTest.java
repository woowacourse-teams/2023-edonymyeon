package edonymyeon.backend.consumption.repository;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.ConsumptionTestSupport;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.ProfileImageInfoTestSupport;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@Import(value = {MemberTestSupport.class, PostTestSupport.class, ConsumptionTestSupport.class,
        ProfileImageInfoTestSupport.class})
@DataJpaTest
class ConsumptionRepositoryTest {

    @Autowired
    private MemberTestSupport memberTestSupport;

    @Autowired
    private PostTestSupport postTestSupport;

    @Autowired
    private ConsumptionTestSupport consumptionTestSupport;

    @Autowired
    private ConsumptionRepository consumptionRepository;

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
}
