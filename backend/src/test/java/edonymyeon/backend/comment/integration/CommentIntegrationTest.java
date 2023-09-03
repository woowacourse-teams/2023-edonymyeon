package edonymyeon.backend.comment.integration;

import static edonymyeon.backend.global.exception.ExceptionInformation.COMMENT_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.COMMENT_MEMBER_NOT_SAME;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class CommentIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    @Test
    void 이미지가_포함된_댓글을_작성한다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();

        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);

        assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static ExtractableResponse<Response> 댓글을_생성한다(
            final Long 게시글_id,
            final File 이미지,
            final String 내용,
            final Member 사용자
    ) {
        return RestAssured.given()
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .multiPart("image", 이미지)
                .multiPart("comment", 내용)
                .when()
                .post("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }

    @Test
    void 이미지가_포함되지_않은_댓글을_작성한다() throws IOException {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();

        final File 빈_이미지 = File.createTempFile("empty", "");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다(게시글.getId(), 빈_이미지, "댓글이다", 댓글_작성자);

        assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 유효하지_않은_게시글에_댓글을_달_수_없다() {
        final Member 댓글_작성자 = memberTestSupport.builder().build();

        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다(-1L, 이미지, "댓글이다", 댓글_작성자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(댓글_생성_응답.jsonPath().getInt("errorCode")).isEqualTo(POST_ID_NOT_FOUND.getCode());
                    assertThat(댓글_생성_응답.jsonPath().getString("errorMessage")).isEqualTo(POST_ID_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    void 댓글_작성자가_댓글을_삭제한다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);

        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글_생성_응답);
        final ExtractableResponse<Response> 댓글_삭제_응답 = 댓글을_삭제한다(게시글.getId(), 댓글_id, 댓글_작성자);

        assertThat(댓글_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 댓글을_삭제한다(
            final Long 게시글_id,
            final Long 댓글_id,
            final Member 사용자
    ) {
        return RestAssured.given()
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .when()
                .delete("/posts/{postId}/comments/{commentId}", 게시글_id, 댓글_id)
                .then()
                .extract();
    }

    @Test
    void 댓글_작성자가_아닌_사람은_댓글을_삭제할_수_없다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);
        final Member 수상한_사람 = memberTestSupport.builder().build();

        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글_생성_응답);
        final ExtractableResponse<Response> 댓글_삭제_응답 = 댓글을_삭제한다(게시글.getId(), 댓글_id, 수상한_사람);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_삭제_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                    assertThat(댓글_삭제_응답.jsonPath().getInt("errorCode")).isEqualTo(COMMENT_MEMBER_NOT_SAME.getCode());
                    assertThat(댓글_삭제_응답.jsonPath().getString("errorMessage")).isEqualTo(
                            COMMENT_MEMBER_NOT_SAME.getMessage());
                }
        );
    }

    @Test
    void path_variable의_게시글_id가_삭제하려는_댓글이_속한_게시글의_id와_같지_않으면_댓글을_삭제할_수_없다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);

        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글_생성_응답);
        final ExtractableResponse<Response> 댓글_삭제_응답 = 댓글을_삭제한다(게시글.getId() + 1, 댓글_id, 댓글_작성자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_삭제_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(댓글_삭제_응답.jsonPath().getInt("errorCode")).isEqualTo(COMMENT_ID_NOT_FOUND.getCode());
                    assertThat(댓글_삭제_응답.jsonPath().getString("errorMessage")).isEqualTo(
                            COMMENT_ID_NOT_FOUND.getMessage());
                }
        );
    }
}
