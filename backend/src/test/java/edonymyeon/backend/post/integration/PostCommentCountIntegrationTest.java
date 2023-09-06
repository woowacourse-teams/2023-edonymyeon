package edonymyeon.backend.post.integration;

import static edonymyeon.backend.comment.integration.steps.CommentSteps.댓글을_삭제한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.CacheConfig;
import edonymyeon.backend.cache.application.HotPostsRedisRepository;
import edonymyeon.backend.cache.util.HotPostCachePolicy;
import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@Import(CacheConfig.class)
public class PostCommentCountIntegrationTest extends IntegrationFixture {

    private final HotPostCachePolicy hotPostCachePolicy;

    private final HotPostsRedisRepository hotPostsRedisRepository;

    private final HotFindingCondition findingCondition = HotFindingCondition.builder().build();

    private Post 댓글이_달린_게시글을_만든다(final int commentCount) {
        final Post 게시글 = postTestSupport.builder().build();
        for (int i = 0; i < commentCount; i++) {
            commentTestSupport.builder().post(게시글).build();
        }
        return 게시글;
    }

    @Test
    void 상세_게시글_조회시_댓글수가_반영되어_조회된다() {
        int commentCount = 3;
        final Post 게시글 = 댓글이_달린_게시글을_만든다(commentCount);
        final Member 사용자 = memberTestSupport.builder().build();

        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(사용자, 게시글.getId());

        assertSoftly(softAssertions -> {
            assertThat(게시글_상세_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(게시글_상세_조회_응답.jsonPath().getInt("reactionCount.commentCount")).isEqualTo(commentCount);
        });
    }

    @Test
    void 상세_게시글_조회시_댓글수가_반영되어_조회된다_그런데_이제_삭제된_댓글수는_빼는것을_곁들인() {
        final Member 사용자 = memberTestSupport.builder().build();
        final Post 게시글 = postTestSupport.builder().build();
        commentTestSupport.builder().post(게시글).build();
        commentTestSupport.builder().post(게시글).build();
        final Comment 삭제될_댓글 = commentTestSupport.builder().post(게시글).member(사용자).build();

        댓글을_삭제한다(게시글.getId(), 삭제될_댓글.getId(), 사용자);
        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(사용자, 게시글.getId());

        assertSoftly(softAssertions -> {
            assertThat(게시글_상세_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(게시글_상세_조회_응답.jsonPath().getInt("reactionCount.commentCount")).isEqualTo(2);
        });
    }

    @Test
    void 전체_게시글_조회시_댓글수가_반영되어_조회된다() {
        Post post1 = 댓글이_달린_게시글을_만든다(1);
        Post post2 = 댓글이_달린_게시글을_만든다(2);
        Post post3 = 댓글이_달린_게시글을_만든다(3);
        Post post4 = 댓글이_달린_게시글을_만든다(4);
        Post post5 = 댓글이_달린_게시글을_만든다(5);

        final var 게시글_전체_조회_응답 = RestAssured
                .when()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_응답.body().jsonPath();

        assertSoftly(softly -> {
                    softly.assertThat(jsonPath.getList("content").size()).isEqualTo(5);
                    softly.assertThat(jsonPath.getLong("content[0].id")).isEqualTo(post5.getId());
                    softly.assertThat(jsonPath.getLong("content[1].id")).isEqualTo(post4.getId());
                    softly.assertThat(jsonPath.getLong("content[2].id")).isEqualTo(post3.getId());
                    softly.assertThat(jsonPath.getLong("content[3].id")).isEqualTo(post2.getId());
                    softly.assertThat(jsonPath.getLong("content[4].id")).isEqualTo(post1.getId());

                    softly.assertThat(jsonPath.getLong("content[0].reactionCount.commentCount")).isEqualTo(5);
                    softly.assertThat(jsonPath.getLong("content[1].reactionCount.commentCount")).isEqualTo(4);
                    softly.assertThat(jsonPath.getLong("content[2].reactionCount.commentCount")).isEqualTo(3);
                    softly.assertThat(jsonPath.getLong("content[3].reactionCount.commentCount")).isEqualTo(2);
                    softly.assertThat(jsonPath.getLong("content[4].reactionCount.commentCount")).isEqualTo(1);
                }
        );
    }

    @Test
    void 핫게시글_조회시_댓글수가_반영되어_조회된다() {
        // 캐시 비우기
        String postIdsCacheKey = hotPostCachePolicy.getKey(findingCondition);
        hotPostsRedisRepository.deleteById(postIdsCacheKey);

        Post post1 = 댓글이_달린_게시글을_만든다(1);
        Post post2 = 댓글이_달린_게시글을_만든다(2);
        Post post3 = 댓글이_달린_게시글을_만든다(3);
        Post post4 = 댓글이_달린_게시글을_만든다(4);
        Post post5 = 댓글이_달린_게시글을_만든다(5);

        JsonPath jsonPath = RestAssured.given()
                .when()
                .get("posts/hot")
                .then()
                .extract()
                .body()
                .jsonPath();

        assertSoftly(softly -> {
                    softly.assertThat(jsonPath.getList("content").size()).isEqualTo(5);
                    softly.assertThat(jsonPath.getLong("content[0].id")).isEqualTo(post1.getId());
                    softly.assertThat(jsonPath.getLong("content[1].id")).isEqualTo(post2.getId());
                    softly.assertThat(jsonPath.getLong("content[2].id")).isEqualTo(post3.getId());
                    softly.assertThat(jsonPath.getLong("content[3].id")).isEqualTo(post4.getId());
                    softly.assertThat(jsonPath.getLong("content[4].id")).isEqualTo(post5.getId());

                    softly.assertThat(jsonPath.getLong("content[0].reactionCount.commentCount")).isEqualTo(1);
                    softly.assertThat(jsonPath.getLong("content[1].reactionCount.commentCount")).isEqualTo(2);
                    softly.assertThat(jsonPath.getLong("content[2].reactionCount.commentCount")).isEqualTo(3);
                    softly.assertThat(jsonPath.getLong("content[3].reactionCount.commentCount")).isEqualTo(4);
                    softly.assertThat(jsonPath.getLong("content[4].reactionCount.commentCount")).isEqualTo(5);
                }
        );
    }

    @Test
    void 게시글_검색시_댓글수가_반영되어_조회된다() {
        final Post 게시글 = postTestSupport.builder()
                .title("사과 먹고 싶어요")
                .content("사먹어도 되나요? 자취생인데...")
                .build();
        commentTestSupport.builder().post(게시글).build();
        commentTestSupport.builder().post(게시글).build();
        commentTestSupport.builder().post(게시글).build();

        final var 검색된_게시글_조회_결과 = RestAssured
                .given()
                .when()
                .queryParam("query", "사과")
                .get("/search")
                .then()
                .extract();

        final var jsonPath = 검색된_게시글_조회_결과.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("content")).hasSize(1);
            softly.assertThat(jsonPath.getString("content[0].title")).contains("사과");
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.commentCount")).isEqualTo(3);
        });
    }
}
