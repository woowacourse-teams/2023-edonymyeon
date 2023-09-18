package edonymyeon.backend.post.integration;

import static edonymyeon.backend.support.IntegrationFixture.CommentSteps.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.CacheConfig;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class MyPostIntegrationTest extends IntegrationFixture {

    @Test
    void 자신이_작성한_게시글을_조회하면_OK를_응답한다() {
        final Member member = memberTestSupport.builder()
                .build();

        final Post post1 = postTestSupport.builder()
                .member(member)
                .build();
        final Post post2 = postTestSupport.builder()
                .member(member)
                .build();

        postTestSupport.builder()
                .member(member)
                .build();

        consumptionTestSupport.builder()
                .post(post1)
                .build();

        consumptionTestSupport.builder()
                .post(post2)
                .build();

        final ExtractableResponse<Response> response = 내가_쓴_글을_조회한다(member);

        final var jsonPath = response.body().jsonPath();
        final List<MyPostResponse> content = jsonPath.getList("content", MyPostResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(content.size()).isEqualTo(3);
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        });
    }

    @Test
    void 자신이_작성한_게시글을_조회시_댓글수와_조회수도_조회된다(@Autowired EntityManager entityManager) {
        final Member member = memberTestSupport.builder()
                .build();
        final Member member2 = memberTestSupport.builder()
                .build();

        final Post post1 = postTestSupport.builder()
                .member(member)
                .build();

        commentTestSupport.builder()
                .post(post1)
                .build();

        게시글_하나를_상세_조회한다(member2, post1.getId());
        게시글_하나를_상세_조회한다(member2, post1.getId());

        final ExtractableResponse<Response> response = 내가_쓴_글을_조회한다(member);

        final var jsonPath = response.body().jsonPath();
        final List<MyPostResponse> content = jsonPath.getList("content", MyPostResponse.class);
        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(content.size()).isEqualTo(1);
            softly.assertThat(content.get(0).reactionCount().commentCount()).isEqualTo(1);
            softly.assertThat(content.get(0).reactionCount().viewCount()).isEqualTo(2);
        });
    }
}
