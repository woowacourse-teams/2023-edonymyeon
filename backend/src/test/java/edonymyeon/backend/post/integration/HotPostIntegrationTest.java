package edonymyeon.backend.post.integration;

import edonymyeon.backend.CacheConfig;
import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.cache.application.BooleanTemplate;
import edonymyeon.backend.cache.application.LongTemplate;
import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.application.PostReadService;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@Import(CacheConfig.class)
public class HotPostIntegrationTest extends IntegrationTest {

    @Autowired
    private ThumbsRepository thumbsRepository;

    @Autowired
    private PostReadService postReadService;

    @Autowired
    private HotPostCachePolicy hotPostCachePolicy;

    @Autowired
    private LongTemplate longTemplate;

    @Autowired
    private BooleanTemplate booleanTemplate;

    private final HotFindingCondition findingCondition = HotFindingCondition.builder().build();

    @BeforeEach
    void 캐시를_삭제하고_시작하도록_한다(){
        String postIdsCacheKey = hotPostCachePolicy.getPostIdsCacheKey(findingCondition);
        String isLastCacheKey = hotPostCachePolicy.getLastCacheKey(findingCondition);

        longTemplate.delete(postIdsCacheKey);
        booleanTemplate.delete(isLastCacheKey);
    }

    @Test
    void 핫게시글이_순서대로_찾아지는지_확인한다() {
        // given
        Post post1 = postTestSupport.builder().build();
        Post post2 = postTestSupport.builder().build();
        Post post3 = postTestSupport.builder().build();
        Post post4 = postTestSupport.builder().build();
        Post post5 = postTestSupport.builder().build();

        Member member = memberTestSupport.builder().build();

        // when
        thumbsRepository.save(new Thumbs(post4, member, ThumbsType.UP)); // 좋아요 1번

        thumbsRepository.save(new Thumbs(post5, member, ThumbsType.UP)); // 좋아요 1번
        postReadService.findSpecificPost(post5.getId(), new ActiveMemberId(member.getId())); // 조회 1번

        // then
        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("posts/hot")
                .then()
                .extract()
                .body()
                .jsonPath();

        assertSoftly(softly -> {
                    softly.assertThat(jsonPath.getList("content").size()).isEqualTo(5);
                    softly.assertThat(jsonPath.getLong("content[0].id")).isEqualTo(post5.getId());
                    softly.assertThat(jsonPath.getLong("content[1].id")).isEqualTo(post4.getId());
                    softly.assertThat(jsonPath.getLong("content[2].id")).isEqualTo(post1.getId());
                    softly.assertThat(jsonPath.getLong("content[3].id")).isEqualTo(post2.getId());
                    softly.assertThat(jsonPath.getLong("content[4].id")).isEqualTo(post3.getId());
                }
        );
    }

    @Test
    void 핫_게시글이_없다면_오래된_순서로_찾아온다() {
        // given, when
        Post post1 = postTestSupport.builder().build();
        Post post2 = postTestSupport.builder().build();
        Post post3 = postTestSupport.builder().build();
        Post post4 = postTestSupport.builder().build();
        Post post5 = postTestSupport.builder().build();

        // then
        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("posts/hot")
                .then()
                .extract()
                .body()
                .jsonPath();

        assertSoftly(softly -> {
                    softly.assertThat(jsonPath.getList("content").size()).isEqualTo(5);
                    softly.assertThat(jsonPath.getLong("content[0].id")).isEqualTo(post1.getId());
                    softly.assertThat(jsonPath.getLong("content[1].id")).isEqualTo(post2.getId());
                    softly.assertThat(jsonPath.getLong("content[2].id")).isEqualTo(post3.getId());
                    softly.assertThat(jsonPath.getLong("content[3].id")).isEqualTo(post4.getId());
                    softly.assertThat(jsonPath.getLong("content[4].id")).isEqualTo(post5.getId());
                }
        );
    }

    @Test
    void 최근_일주일_이내의_글이_없다면_빈리스트가_조회된다() {
        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("posts/hot")
                .then()
                .extract()
                .body()
                .jsonPath();

        assertThat(jsonPath.getList("content").size()).isEqualTo(0);
    }
}
