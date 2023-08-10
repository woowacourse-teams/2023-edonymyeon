package edonymyeon.backend.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.EdonymyeonTest;
import edonymyeon.backend.TestConfig;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@Import(TestConfig.class)
@EdonymyeonTest
public class PostViewTest implements ImageFileCleaner {

    private final PostReadService postReadService;
    private final PostTestSupport postTestSupport;
    private final MemberTestSupport memberTestSupport;
    private final PostRepository postRepository;

    private Post post;
    private Member writer;
    private Member viewer;

    @BeforeEach
    void setUp() {
        writer = memberTestSupport.builder().build();
        viewer = memberTestSupport.builder().build();
        post = postTestSupport.builder()
                .member(writer)
                .build();
    }

    @Test
    void 글을_처음_작성하면_조회수는_0으로_설정된다() {
        assertThat(post.getViewCount()).isEqualTo(0);
    }

    @Test
    void 작성자_본인이_글을_조회하는_경우_조회수는_올라가지_않는다() {
        assertThat(post.getViewCount()).isEqualTo(0);
        postReadService.findSpecificPost(post.getId(), new ActiveMemberId(writer.getId()));

        assertThat(post.getViewCount()).isEqualTo(0);
    }

    @Test
    void 타인이_조회하는_경우_공백_기간_없이_조회수가_증가한다() {
        assertThat(post.getViewCount()).isEqualTo(0);
        postReadService.findSpecificPost(post.getId(), new ActiveMemberId(viewer.getId()));
        postReadService.findSpecificPost(post.getId(), new ActiveMemberId(viewer.getId()));
        postReadService.findSpecificPost(post.getId(), new ActiveMemberId(viewer.getId()));

        assertThat(postRepository.findById(post.getId()).get().getViewCount()).isEqualTo(3);
    }

    @Test
    void 로그인하지_않은_사용자가_조회하는_경우에도_조회수는_증가한다() {
        assertThat(post.getViewCount()).isEqualTo(0);
        postReadService.findSpecificPost(post.getId(), new AnonymousMemberId());
        postReadService.findSpecificPost(post.getId(), new AnonymousMemberId());
        postReadService.findSpecificPost(post.getId(), new AnonymousMemberId());

        assertThat(postRepository.findById(post.getId()).get().getViewCount()).isEqualTo(3);
    }
}

