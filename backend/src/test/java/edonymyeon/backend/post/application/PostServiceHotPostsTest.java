package edonymyeon.backend.post.application;

import edonymyeon.backend.CacheConfig;
import edonymyeon.backend.cache.application.BooleanTemplate;
import edonymyeon.backend.cache.application.LongTemplate;
import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@Import(CacheConfig.class)
@RequiredArgsConstructor
public class PostServiceHotPostsTest {

    private final HotPostCachePolicy hotPostCachePolicy;

    private final LongTemplate longTemplate;

    private final BooleanTemplate booleanTemplate;

    private final PostTestSupport postTestSupport;

    private final PostReadService postReadService;

    private final HotFindingCondition findingCondition = HotFindingCondition.builder().build();

    private String postIdsCacheKey;

    private String isLastCacheKey;

    @BeforeEach
    void 새글을_두개_등록하고_남아있는_캐시를_삭제한다() {
        postIdsCacheKey = hotPostCachePolicy.getPostIdsCacheKey(findingCondition);
        isLastCacheKey = hotPostCachePolicy.getLastCacheKey(findingCondition);

        longTemplate.delete(postIdsCacheKey);
        booleanTemplate.delete(isLastCacheKey);
    }

    @Test
    void 테스트용_키가_제대로_생성되는지_확인한다() {
        assertSoftly(softly -> {
                    softly.assertThat(postIdsCacheKey).contains("TEST-HOT", "SIZE=" + findingCondition.getSize(), "PAGE=" + findingCondition.getPage());
                    softly.assertThat(isLastCacheKey).contains("TEST-HOT", "LAST", "SIZE=" + findingCondition.getSize(), "PAGE=" + findingCondition.getPage());
                }
        );
    }

    @Test
    void 캐시가_존재하지_않아도_조회되고_새로_캐싱한다() {
        postTestSupport.builder().build();
        postTestSupport.builder().build();

        var hotPosts = postReadService.findHotPosts(findingCondition).getContent();

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts).hasSize(2);
                    softly.assertThat(longTemplate.hasCache(postIdsCacheKey)).isTrue();
                    softly.assertThat(booleanTemplate.hasCache(isLastCacheKey)).isTrue();
                }
        );
    }

    @Test
    void 캐싱이_되어있으면_조회된다() {
        postTestSupport.builder().build();
        postTestSupport.builder().build();

        var hotPosts = postReadService.findHotPosts(findingCondition).getContent();
        var hotPostFromCache = postReadService.findHotPosts(findingCondition).getContent();

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts.size()).isEqualTo(hotPostFromCache.size());
                    softly.assertThat(hotPosts).containsAll(hotPostFromCache);
                }
        );
    }

    @Test
    void 최근_글이_없으면_캐싱에_저장하지_않고_빈리스트가_조회된다() {
        var hotPosts = postReadService.findHotPosts(findingCondition).getContent();

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts).isEmpty();
                    softly.assertThat(longTemplate.hasCache(postIdsCacheKey)).isFalse();
                    softly.assertThat(booleanTemplate.hasCache(isLastCacheKey)).isFalse();
                }
        );
    }
}
