package edonymyeon.backend.post.repository;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.EdonymyeonTest;
import edonymyeon.backend.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.application.PostReadService;
import edonymyeon.backend.post.domain.HotPostPolicy;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.ThumbsUpPostTestSupport;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@EdonymyeonTest
class PostRepositoryTest {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final PostReadService postReadService;

    private final ThumbsUpPostTestSupport thumbsPostTestSupport;

    private final PostTestSupport postTestSupport;

    private final Pageable page = HotFindingCondition.of(0,5).toPage();

    private Member member;

    @BeforeEach
    public void setUp() {
        member = new Member(
                "email",
                "password123!",
                "nickname",
                null
        );
        memberRepository.save(member);
    }

    @Test
    void 생성() {
        Post post = new Post("호바", "호이 바보라는 뜻", 0L, member);
        postRepository.save(post);

        Post target = postRepository.findById(post.getId()).get();

        assertSoftly(softly -> {
                    softly.assertThat(target.getId()).isNotNull();
                    softly.assertThat(target.getTitle()).isEqualTo("호바");
                    softly.assertThat(target.getCreatedAt()).isNotNull();
                    softly.assertThat(target.getViewCount()).isEqualTo(0L);
                    softly.assertThat(target.getMember().getId()).isEqualTo(member.getId());
                }
        );
    }

    @Test
    void 최근_게시글이_없다면_빈_리스트가_조회된다() {
        Slice<Post> hotPosts = postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.VIEW_COUNT_WEIGHT,
                HotPostPolicy.THUMBS_COUNT_WEIGHT,
                page);

        Assertions.assertThat(hotPosts.isEmpty()).isTrue();
    }

    @Test
    void findHotPosts_메서드에서_thumbs_테이블에_정보가_없어도_제대로_조인되는지_확인한다() {
        // given
        Post post1 = postTestSupport.builder().build();
        Post post2 = postTestSupport.builder().build();

        // when
        thumbsPostTestSupport.builder().post(post1).build();

        // then
        Slice<Post> hotPosts = postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.VIEW_COUNT_WEIGHT,
                HotPostPolicy.THUMBS_COUNT_WEIGHT,
                page);

        for (Post hotPost : hotPosts) {
            System.out.println("hotPost = " + hotPost.getId() + hotPost.getTitle());
        }

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts.get().toList().size()).isEqualTo(2);
                    softly.assertThat(hotPosts.get().toList().get(0).getId()).isEqualTo(post1.getId());
                    softly.assertThat(hotPosts.get().toList().get(1).getId()).isEqualTo(post2.getId());
                }
        );
    }

    @Test
    void 인기순_대로_게시글이_조회되는지_확인한다() {
        // given
        Post score8Post = postTestSupport.builder().build();
        Post score6Post = postTestSupport.builder().build();
        Post score9Post = postTestSupport.builder().build();
        Post score10Post = postTestSupport.builder().build();
        Post score5Post = postTestSupport.builder().build();

        // when
        thumbsPostTestSupport.builder().post(score8Post).build();
        thumbsPostTestSupport.builder().post(score8Post).build();
        postReadService.findSpecificPost(score8Post.getId(), new AnonymousMemberId());
        postReadService.findSpecificPost(score8Post.getId(), new AnonymousMemberId());

        thumbsPostTestSupport.builder().post(score6Post).build();
        thumbsPostTestSupport.builder().post(score6Post).build();

        thumbsPostTestSupport.builder().post(score9Post).build();
        thumbsPostTestSupport.builder().post(score9Post).build();
        thumbsPostTestSupport.builder().post(score9Post).build();

        thumbsPostTestSupport.builder().post(score10Post).build();
        thumbsPostTestSupport.builder().post(score10Post).build();
        thumbsPostTestSupport.builder().post(score10Post).build();
        postReadService.findSpecificPost(score10Post.getId(), new AnonymousMemberId());

        thumbsPostTestSupport.builder().post(score5Post).build();
        postReadService.findSpecificPost(score5Post.getId(), new AnonymousMemberId());
        postReadService.findSpecificPost(score5Post.getId(), new AnonymousMemberId());

        // then
        Slice<Post> hotPosts = postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.VIEW_COUNT_WEIGHT,
                HotPostPolicy.THUMBS_COUNT_WEIGHT,
                page);

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts.get().toList().size()).isEqualTo(5);
                    softly.assertThat(hotPosts.get().toList().get(0).getId()).isEqualTo(score10Post.getId());
                    softly.assertThat(hotPosts.get().toList().get(1).getId()).isEqualTo(score9Post.getId());
                    softly.assertThat(hotPosts.get().toList().get(2).getId()).isEqualTo(score8Post.getId());
                    softly.assertThat(hotPosts.get().toList().get(3).getId()).isEqualTo(score6Post.getId());
                    softly.assertThat(hotPosts.get().toList().get(4).getId()).isEqualTo(score5Post.getId());
                }
        );
    }

    @Test
    void 동점일_경우_오래된_순서로_정렬된다() {
        // given
        Post score0Post1 = postTestSupport.builder().build();
        Post score0Post2 = postTestSupport.builder().build();
        Post score0Post3 = postTestSupport.builder().build();
        Post score1Post1 = postTestSupport.builder().build();
        Post score1Post2 = postTestSupport.builder().build();

        // when
        postReadService.findSpecificPost(score1Post1.getId(), new AnonymousMemberId());
        postReadService.findSpecificPost(score1Post2.getId(), new AnonymousMemberId());

        // then
        Slice<Post> hotPosts = postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.VIEW_COUNT_WEIGHT,
                HotPostPolicy.THUMBS_COUNT_WEIGHT,
                page);

        assertSoftly(softly -> {
                    softly.assertThat(hotPosts.get().toList().size()).isEqualTo(5);
                    softly.assertThat(hotPosts.get().toList().get(0).getId()).isEqualTo(score1Post1.getId());
                    softly.assertThat(hotPosts.get().toList().get(1).getId()).isEqualTo(score1Post2.getId());
                    softly.assertThat(hotPosts.get().toList().get(2).getId()).isEqualTo(score0Post1.getId());
                    softly.assertThat(hotPosts.get().toList().get(3).getId()).isEqualTo(score0Post2.getId());
                    softly.assertThat(hotPosts.get().toList().get(4).getId()).isEqualTo(score0Post3.getId());
                }
        );
    }
}
