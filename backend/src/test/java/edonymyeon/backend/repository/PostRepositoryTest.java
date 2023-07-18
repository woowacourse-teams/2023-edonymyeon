package edonymyeon.backend.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@Sql("/dummydata.sql")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest
class PostRepositoryTest {

    private final PostRepository posts;

    private final MemberRepository members;

    private Member member;

    @BeforeEach
    public void setUp() {
        this.member = members.findById(1L).get();
    }

    @Test
    void 생성() {
        Post post = new Post("호바", "호이 바보라는 뜻", 0L, member);
        posts.save(post);

        Post target = posts.findById(post.getId()).get();

        assertSoftly(softly -> {
                    softly.assertThat(target.getId()).isNotNull();
                    softly.assertThat(target.getTitle()).isEqualTo("호바");
                    softly.assertThat(target.getCreateAt()).isNotNull();
                    softly.assertThat(target.getViewCount()).isEqualTo(0);
                }
        );
    }
}
