package edonymyeon.backend.report.ui;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.content.comment.domain.Comment;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.content.post.ImageFileCleaner;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.support.EdonymyeonRestAssured;
import edonymyeon.backend.support.IntegrationFixture;
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

        final ReportRequest reportRequest = new ReportRequest("POST", postId, 4, null);

        final ExtractableResponse<Response> 게시글_신고_응답 = 신고를_한다(member, reportRequest);

        assertThat(게시글_신고_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(게시글_신고_응답.header("location")).isNotNull();
    }

    @Test
    void 특정_댓글을_신고한다() {
        final Member member = 사용자를_하나_만든다();
        final Comment 댓글 = commentTestSupport.builder().build();

        final ReportRequest reportRequest = new ReportRequest("COMMENT", 댓글.getId(), 4, null);

        final ExtractableResponse<Response> 댓글_신고_응답 = 신고를_한다(member, reportRequest);

        assertThat(댓글_신고_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(댓글_신고_응답.header("location")).isNotNull();
    }

    @Test
    void 로그인하지_않은_사용자는_신고하지_못한다() {
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> post = 게시글을_하나_만든다(member);
        final long postId = 응답의_location헤더에서_id를_추출한다(post);

        final ReportRequest reportRequest = new ReportRequest("POST", postId, 4, null);

        final ExtractableResponse<Response> 게시글_신고_응답 = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reportRequest)
                .when()
                .post("/report")
                .then()
                .extract();

        assertThat(게시글_신고_응답.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 요청_바디에_신고_타입이_제대로_들어있지_않다면_예외가_발생한다() {
        // given
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> post = 게시글을_하나_만든다(member);
        final long postId = 응답의_location헤더에서_id를_추출한다(post);
        // when

        // then
        final ReportRequest reportRequest = new ReportRequest(null, postId, 4, null);

        final ExtractableResponse<Response> 게시글_신고_응답 = 신고를_한다(member, reportRequest);

        assertThat(게시글_신고_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 요청_바디에_신고_카테고리가_제대로_들어있지_않다면_예외가_발생한다() {
        // given
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> post = 게시글을_하나_만든다(member);
        final long postId = 응답의_location헤더에서_id를_추출한다(post);

        // when

        // then
        final ReportRequest reportRequest = new ReportRequest(null, postId, 10, null);

        final ExtractableResponse<Response> 게시글_신고_응답 = 신고를_한다(member, reportRequest);

        assertThat(게시글_신고_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
