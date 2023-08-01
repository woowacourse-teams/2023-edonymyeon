package edonymyeon.backend.post.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(TestConfig.class)
@SpringBootTest
public class PostViewTest implements ImageFileCleaner {

    private final PostService postService;
    private final PostTestSupport postTestSupport;
    private final MemberTestSupport memberTestSupport;

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
        postService.findSpecificPost(post.getId(), new MemberId(writer.getId()));

        assertThat(post.getViewCount()).isEqualTo(0);
    }

    @Test
    void 타인이_조회하는_경우_공백_기간_없이_조회수가_증가한다() {
        assertThat(post.getViewCount()).isEqualTo(0);
        postService.findSpecificPost(post.getId(), new MemberId(viewer.getId()));
        postService.findSpecificPost(post.getId(), new MemberId(viewer.getId()));
        postService.findSpecificPost(post.getId(), new MemberId(viewer.getId()));

        assertThat(post.getViewCount()).isEqualTo(3);
    }

    @Test
    void 로그인하지_않은_사용자가_조회하는_경우에도_조회수는_증가한다() {
        assertThat(post.getViewCount()).isEqualTo(0);
        postService.findSpecificPost(post.getId(), new AnonymousMemberId());
        postService.findSpecificPost(post.getId(), new AnonymousMemberId());
        postService.findSpecificPost(post.getId(), new AnonymousMemberId());

        assertThat(post.getViewCount()).isEqualTo(3);
    }
}

