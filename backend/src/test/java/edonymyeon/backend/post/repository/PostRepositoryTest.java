package edonymyeon.backend.post.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.EdonymyeonTest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@EdonymyeonTest
class PostRepositoryTest {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;
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
}
