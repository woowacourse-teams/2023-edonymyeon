package edonymyeon.backend.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.image.postimage.ProfileImageInfo;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PostFindingSpecificPostTest {

    private final PostService postService;
    private Long memberId;
    private Long postId;

    @BeforeEach
    @Test
    void setUp(
            @Autowired PostRepository postRepository,
            @Autowired MemberRepository memberRepository
    ) {
        final var member = new Member(
                "email",
                "password",
                "nick",
                new ProfileImageInfo("/static/img/file/test_image_1.jpg", "test_image_1.jpg")
        );
        memberId = memberRepository.save(member).getId();

        final var post = new Post(
                "Summer Breeze",
                "Enjoy the refreshing summer breeze and soak up the sun's warmth at the beach.",
                14_000L,
                member
        );
        postId = postRepository.save(post).getId();
    }

    @Test
    void 게시글아이디가_주어지면_게시글의_상세정보를_알려준다() {
        final var postInfoResponse = postService.findSpecificPost(postId, memberId);

        assertAll(
                () -> assertThat(postInfoResponse.title()).isEqualTo("Summer Breeze"),
                () -> assertThat(postInfoResponse.content()).isEqualTo("Enjoy the refreshing summer breeze and soak up the sun's warmth at the beach."),

                () -> assertThat(postInfoResponse.price()).isEqualTo(14_000L),
                () -> assertThat(postInfoResponse.createdAt()).isNotNull(),
                () -> assertThat(postInfoResponse.upCount()).isEqualTo(0),
                () -> assertThat(postInfoResponse.downCount()).isEqualTo(0),

                () -> assertThat(postInfoResponse.isUp()).isFalse(),
                () -> assertThat(postInfoResponse.isDown()).isFalse(),
                () -> assertThat(postInfoResponse.isScrap()).isFalse(),
                () -> assertThat(postInfoResponse.isWriter()).isFalse()
        );
    }

    @Test
    void 로그인_되어있지_않으면_조회는_가능하되_추천여부와_스크랩여부와_작성자여부는_모두_false이다() {
        final var postInfoResponse = postService.findSpecificPost(postId, null);

        assertThat(postInfoResponse.isUp()).isFalse();
        assertThat(postInfoResponse.isDown()).isFalse();
        assertThat(postInfoResponse.isScrap()).isFalse();
        assertThat(postInfoResponse.isWriter()).isFalse();
    }

    @Test
    void 작성자_본인이_본인_게시글을_보는경우_isWriter값이_true이다() {
        final var postInfoResponse = postService.findSpecificPost(postId, memberId);

        assertThat(postInfoResponse.isWriter()).isTrue();
    }

    @Test
    void 타인의_게시글을_보는경우_isWriter값이_false이다() {
        final var postInfoResponse = postService.findSpecificPost(postId, memberId);

        assertThat(postInfoResponse.isWriter()).isFalse();
    }
}
