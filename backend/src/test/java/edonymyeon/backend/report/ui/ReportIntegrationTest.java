package edonymyeon.backend.report.ui;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.report.application.ReportRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
class ReportIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    @Test
    void 특정_게시글을_신고한다() {
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> post = 게시글을_하나_만든다(member);
        final long postId = 응답의_location헤더에서_id를_추출한다(post);

        final ReportRequest reportRequest = new ReportRequest(postId, 4, null);

        final ExtractableResponse<Response> 게시글_신고_응답 = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reportRequest)
                .when()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .post("/report")
                .then()
                .extract();

        assertThat(게시글_신고_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(게시글_신고_응답.header("location")).isNotNull();
    }

    @Test
    void 로그인하지_않은_사용자는_신고하지_못한다() {
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> post = 게시글을_하나_만든다(member);
        final long postId = 응답의_location헤더에서_id를_추출한다(post);

        final ReportRequest reportRequest = new ReportRequest(postId, 4, null);

        final ExtractableResponse<Response> 게시글_신고_응답 = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reportRequest)
                .when()
                .post("/report")
                .then()
                .extract();

        assertThat(게시글_신고_응답.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
