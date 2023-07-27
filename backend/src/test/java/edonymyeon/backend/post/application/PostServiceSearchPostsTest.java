package edonymyeon.backend.post.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.support.MemberTestSupport;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
@SpringBootTest
public class PostServiceSearchPostsTest {

    private final PostService postService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final MemberTestSupport memberTestSupport;

    private Long postId1;

    private Long postId2;

    final GeneralFindingCondition generalFindingCondition = GeneralFindingCondition.of(null, null, null, null);


    @BeforeEach
    void setUp() {
        final Member member = saveMember();

        postId1 = savePost(member, "애플 먹고 싶어요", "사먹어도 되나요? 자취생인데...", 14_000L);
        postId2 = savePost(member, "사과 먹고 싶어요", "사먹어도 되나요? 자취생인데...", 14_000L);
    }

    @Test
    void 제목으로_검색이_제대로_되는지_확인한다() {
        // when
        List<GeneralPostInfoResponse> 검색결과 = postService.searchPosts("사과", generalFindingCondition);

        // then
        assertSoftly(softly -> {
                    softly.assertThat(검색결과.size()).isEqualTo(1);
                    softly.assertThat(검색결과.get(0).id()).isEqualTo(postId2);
                }
        );
    }

    @Test
    void 내용으로_검색이_제대로_되는지_확인한다() {
        // when
        List<GeneralPostInfoResponse> 검색결과 = postService.searchPosts("자취생", generalFindingCondition);

        // then
        assertThat(검색결과.size()).isEqualTo(2);
    }

    @Test
    void 내용으로_검색시_최신순으로_조회가_된다() {
        // when
        List<GeneralPostInfoResponse> 검색결과 = postService.searchPosts("자취생", generalFindingCondition);

        // then
        assertSoftly(softly -> {
                    softly.assertThat(검색결과.size()).isEqualTo(2);
                    softly.assertThat(검색결과.get(0).id()).isEqualTo(postId2);
                    softly.assertThat(검색결과.get(1).id()).isEqualTo(postId1);
                }
        );
    }

    @Test
    void 검색어_결과가_없을때_결과값은_빈리스트_이다() {
        // when
        List<GeneralPostInfoResponse> 검색결과 = postService.searchPosts("이리내", generalFindingCondition);

        // then
        assertThat(검색결과.size()).isEqualTo(0);
    }


    private Long savePost(
            final Member member,
            final String title,
            final String content,
            final Long price
    ) {
        final var post = new Post(title, content, price, member);
        return postRepository.save(post).getId();
    }

    private Member saveMember() {
        final Member member = memberTestSupport.builder()
                .build();
        memberRepository.save(member);
        return member;
    }
}
