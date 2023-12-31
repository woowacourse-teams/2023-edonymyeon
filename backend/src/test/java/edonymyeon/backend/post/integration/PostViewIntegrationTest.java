package edonymyeon.backend.post.integration;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
public class PostViewIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    @Test
    void 게시글_조회수_시나리오() {
        final Member 작성자 = this.사용자를_하나_만든다();
        final Member 열람자 = this.사용자를_하나_만든다();
        final Post 게시글 = this.postTestSupport.builder()
                .member(작성자)
                .build();

        작성자가_자신의_글을_조회하면_조회수가_오르지_않는다(작성자, 게시글);
        for (int count = 1; count <= 4; count++) {
            다른사람이_글을_조회하면_조회수가_오른다(열람자, 게시글, count);
        }
    }

    private void 다른사람이_글을_조회하면_조회수가_오른다(final Member 열람자, final Post 게시글, final int expected) {
        final ExtractableResponse<Response> 다른사람이_게시글_조회한_결과 = this.게시글_하나를_상세_조회한다(열람자, 게시글.getId());
        assertThat(다른사람이_게시글_조회한_결과.body()
                .jsonPath()
                .getLong("reactionCount.viewCount"))
                .isEqualTo(expected);
    }

    private void 작성자가_자신의_글을_조회하면_조회수가_오르지_않는다(final Member 작성자, final Post 게시글) {
        다른사람이_글을_조회하면_조회수가_오른다(작성자, 게시글, 0);
    }
}
