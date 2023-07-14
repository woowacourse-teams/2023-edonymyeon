package edonymyeon.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.PostResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest
class PostServiceTest {

    private final PostService postService;

    public PostServiceTest(final PostService postService) {
        this.postService = postService;
    }

    @Test
    void 게시글_생성() {

        final PostRequest request = new PostRequest(
                "사도 돼요?",
                "얼마 안해요",
                100_000L
        );

        final PostResponse target = postService.createPost(request);
        assertThat(target.id()).isNotNull();
    }
}
