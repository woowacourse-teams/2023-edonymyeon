package edonymyeon.backend.content.post.application;

import edonymyeon.backend.CacheConfig;
import edonymyeon.backend.content.cache.application.PostCachingService;
import edonymyeon.backend.content.cache.repository.HotPostsRepository;
import edonymyeon.backend.content.cache.util.HotPostCachePolicy;
import edonymyeon.backend.content.post.application.HotFindingCondition;
import edonymyeon.backend.content.post.application.PostReadService;
import edonymyeon.backend.content.post.application.PostSlice;
import edonymyeon.backend.content.post.application.dto.response.GeneralPostInfoResponse;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.content.post.repository.PostRepository;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@Import(CacheConfig.class)
@RequiredArgsConstructor
@IntegrationTest
public class PostServiceHotPostsTest {

    private final HotPostCachePolicy hotPostCachePolicy;

    private final PostTestSupport postTestSupport;

    private final PostReadService postReadService;

    private final PostRepository postRepository;

    private final PostCachingService postCachingService;

    private final HotFindingCondition findingCondition = HotFindingCondition.builder().build();

    private String postIdsCacheKey;

    @BeforeEach
    void 새글을_두개_등록하고_남아있는_캐시를_삭제한다() {
        postIdsCacheKey = hotPostCachePolicy.getKey(findingCondition);
        HotPostsRepository.delete(postIdsCacheKey);
    }

    @Test
    void 테스트용_키가_제대로_생성되는지_확인한다() {
        assertThat(postIdsCacheKey).contains("TEST", "SIZE=" + findingCondition.getSize(), "PAGE=" + findingCondition.getPage());
    }

    @Test
    void 캐시가_존재하지_않아도_조회되고_새로_캐싱한다() {
        final Post post1 = postTestSupport.builder().build();
        final Post post2 = postTestSupport.builder().build();

        var hotPosts = postReadService.findHotPosts(findingCondition);

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts.getContent()).hasSize(2);
                    softly.assertThat(hotPosts.getContent().get(0).id()).isEqualTo(post2.getId());
                    softly.assertThat(hotPosts.getContent().get(1).id()).isEqualTo(post1.getId());
                    softly.assertThat(hotPosts.isLast()).isTrue();
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
    void 캐싱이_만료되면_새로운_내역으로_조회되는지_확인한다() throws InterruptedException {
        // given
        postTestSupport.builder().build();

        PostSlice<GeneralPostInfoResponse> hotPosts1 = postReadService.findHotPosts(findingCondition);
        assertThat(hotPosts1.getContent()).hasSize(1);

        // when
        Thread.sleep(1000);
        assertThat(postCachingService.shouldRefreshCache(findingCondition)).isTrue();

        postTestSupport.builder().build();
        postTestSupport.builder().build();
        postTestSupport.builder().build();
        postTestSupport.builder().build();
        postTestSupport.builder().build();

        // then
        PostSlice<GeneralPostInfoResponse> hotPosts = postReadService.findHotPosts(findingCondition);
        assertSoftly(softly -> {
                    softly.assertThat(hotPosts.getContent().size()).isEqualTo(5);
                    softly.assertThat(hotPosts.isLast()).isFalse();
                }
        );
    }

    @Test
    void 빈_캐싱이_만료되면_새로운_내역으로_조회되는지_확인한다() throws InterruptedException {
        // given
        var 빈_핫게시글 = postReadService.findHotPosts(findingCondition).getContent();
        assertThat(빈_핫게시글).isEmpty();

        // when
        Thread.sleep(1000);
        assertThat(postCachingService.shouldRefreshCache(findingCondition)).isTrue();

        Post 새로운_게시글 = postTestSupport.builder().build();

        // then
        var 만료_후_조회한_핫_게시글 = postReadService.findHotPosts(findingCondition)
                .getContent()
                .stream()
                .map(GeneralPostInfoResponse::id)
                .toList();

        assertSoftly(softly -> {
                    softly.assertThat(만료_후_조회한_핫_게시글.size()).isEqualTo(1);
                    softly.assertThat(만료_후_조회한_핫_게시글.contains(새로운_게시글.getId())).isTrue();
                }
        );
    }

    @Test
    void 캐싱된_게시글이_삭제되면_새로운_게시글이_조회된다() {
        // given
        postTestSupport.builder().build();
        postTestSupport.builder().build();
        var 삭제할_게시글 = postTestSupport.builder().build();
        postTestSupport.builder().build();
        postTestSupport.builder().build();
        var 여섯번째로_작성된_게시글 = postTestSupport.builder().build();

        var 삭제_전_조회한_핫게시글 = postReadService.findHotPosts(findingCondition)
                .getContent()
                .stream()
                .map(GeneralPostInfoResponse::id)
                .toList();
        assertSoftly(softly -> {
                    softly.assertThat(삭제_전_조회한_핫게시글.contains(삭제할_게시글.getId())).isTrue();
                    softly.assertThat(삭제_전_조회한_핫게시글.size()).isEqualTo(5);
                    softly.assertThat(삭제_전_조회한_핫게시글.contains(여섯번째로_작성된_게시글.getId())).isFalse();
                }
        );

        // when
        postRepository.deleteById(삭제할_게시글.getId());
        var 핫게시글을_삭제하고_난_뒤에_조회한_핫게시글 = postReadService.findHotPosts(findingCondition)
                .getContent()
                .stream()
                .map(GeneralPostInfoResponse::id)
                .toList();

        // then
        assertSoftly(softly -> {
                    softly.assertThat(핫게시글을_삭제하고_난_뒤에_조회한_핫게시글.contains(삭제할_게시글.getId())).isFalse();
                    softly.assertThat(핫게시글을_삭제하고_난_뒤에_조회한_핫게시글.size()).isEqualTo(5);
                    softly.assertThat(핫게시글을_삭제하고_난_뒤에_조회한_핫게시글.contains(여섯번째로_작성된_게시글.getId())).isTrue();
                }
        );
    }

    @Test
    void 최근_글이_없으면_빈리스트가_조회된다() {
        var hotPosts = postReadService.findHotPosts(findingCondition).getContent();
        assertThat(hotPosts).isEmpty();
    }

    @Test
    void 빈_핫게시글이_캐싱_되어있다면_빈리스트가_조회된다() {
        postReadService.findHotPosts(findingCondition);
        var hotPosts = postReadService.findHotPosts(findingCondition).getContent();
        assertThat(hotPosts).isEmpty();
    }
}
