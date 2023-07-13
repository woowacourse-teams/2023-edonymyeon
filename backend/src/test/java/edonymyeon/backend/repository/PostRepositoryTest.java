package edonymyeon.backend.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.domain.Post;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest
class PostRepositoryTest {

    private final PostRepository posts;

    PostRepositoryTest(final PostRepository posts) {
        this.posts = posts;
    }

    @Test
    void 생성() {
        Post post = new Post("호바", "호이 바보라는 뜻", 0L);
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
