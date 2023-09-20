package edonymyeon.backend.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.application.NotificationSender;
import edonymyeon.backend.report.application.ReportRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@IntegrationTest
public class IntegrationFixture {

    protected static final File 이미지1 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
    protected static final File 이미지2 = new File("./src/test/resources/static/img/file/test_image_2.jpg");

    @Autowired
    protected ProfileImageInfoTestSupport profileImageInfoTestSupport;

    @Autowired
    protected TestMemberBuilder memberTestSupport;

    @Autowired
    protected PostTestSupport postTestSupport;

    @Autowired
    protected PostImageInfoTestSupport postImageInfoTestSupport;

    @Autowired
    protected PostIntegrationTestSupport postIntegrationTestSupport;

    @Autowired
    protected ThumbsUpPostTestSupport thumbsUpPostTestSupport;

    @Autowired
    protected ConsumptionTestSupport consumptionTestSupport;

    @Autowired
    protected CommentTestSupport commentTestSupport;

    @MockBean
    protected NotificationSender notificationSender;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        알림전송기능을_모킹한다();
    }

    protected Member 사용자를_하나_만든다() {
        return memberTestSupport.builder().build();
    }

    protected long 응답의_location헤더에서_id를_추출한다(final ExtractableResponse<Response> 요청에_대한_응답) {
        final String location = 요청에_대한_응답.header("location");
        final String[] split = location.split("/");
        return Long.parseLong(split[split.length - 1]);
    }

    private void 알림전송기능을_모킹한다() {
        doNothing().when(notificationSender).sendNotification(any(), any());
    }

    /**
     * 인증
     */
    protected String 로그인(final Member member) {
        final LoginRequest request = new LoginRequest(member.getEmail(), TestMemberBuilder.getRawPassword(),
                "testToken");
        return EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/login")
                .then()
                .extract()
                .sessionId();
    }

    protected String 로그인(final String email, final String password) {
        final LoginRequest request = new LoginRequest(email, password, "testToken");
        return EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/login")
                .then()
                .extract()
                .sessionId();
    }

    /**
     * 게시글
     */
    protected ExtractableResponse<Response> 게시글을_하나_만든다(final Member member) {
        return postIntegrationTestSupport.builder()
                .member(member)
                .title("this is title")
                .content("this is content")
                .price(1000L)
                .image1(이미지1)
                .image2(이미지2)
                .build();
    }

    protected ExtractableResponse<Response> 게시글을_삭제한다(final Member 작성자, final long 게시글_id) {
        final String sessionId = 로그인(작성자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .delete("posts/" + 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 게시글_하나를_상세_조회한다(final Member 열람인, final long 게시글_id) {
        final String sessionId = 로그인(열람인);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .get("/posts/" + 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 게시글_하나를_상세_조회한다(final long 게시글_id) {
        return EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .when()
                .get("/posts/" + 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 게시글을_전체_조회한다() {
        return EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .when()
                .get("/posts")
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 핫_게시글을_조회한다() {
        return EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .when()
                .get("posts/hot")
                .then()
                .extract();
    }

    /**
     * 댓글
     */
    protected ExtractableResponse<Response> 댓글을_생성한다_이미지포함(
            final Long 게시글_id,
            final File 이미지,
            final String 내용,
            final Member 사용자
    ) {
        final String sessionId = 로그인(사용자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("image", 이미지)
                .multiPart("comment", 내용)
                .when()
                .post("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 댓글을_생성한다_이미지없이(
            final Long 게시글_id,
            final String 내용,
            final Member 사용자
    ) throws IOException {
        final File 빈_이미지 = File.createTempFile("empty", "");
        final String sessionId = 로그인(사용자);
        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("image", 빈_이미지)
                .multiPart("comment", 내용)
                .when()
                .post("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 댓글을_삭제한다(
            final Long 게시글_id,
            final Long 댓글_id,
            final Member 사용자
    ) {
        final String sessionId = 로그인(사용자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .delete("/posts/{postId}/comments/{commentId}", 게시글_id, 댓글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 게시물에_대한_댓글을_모두_조회한다(
            final Long 게시글_id,
            final Member 사용자
    ) {
        final String sessionId = 로그인(사용자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .get("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 게시물에_대한_댓글을_모두_조회한다(final Long 게시글_id) {
        return EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .when()
                .get("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 내가_쓴_글을_조회한다(final Member member) {
        final String sessionId = 로그인(member);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .get("/profile/my-posts")
                .then()
                .extract();
    }

    /**
     * 소비확정/절약확정
     */
    protected ExtractableResponse<Response> 특정_기간의_소비금액을_확인한다(final Integer 기간, final Member 사용자) {
        final String sessionId = 로그인(사용자);

        final ExtractableResponse<Response> 조회_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/consumptions?period-month={periodMonth}", 기간)
                .then()
                .extract();
        return 조회_응답;
    }

    protected ExtractableResponse<Response> 구매_확정_요청을_보낸다(
            final Member 사용자,
            final Long 게시글_id,
            final PurchaseConfirmRequest 구매_확정_요청
    ) {
        final String sessionId = 로그인(사용자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(구매_확정_요청)
                .when()
                .post("/profile/my-posts/{postId}/purchase-confirm", 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 절약_확정_요청을_보낸다(
            final Member 사용자,
            final Long 게시글_id,
            final SavingConfirmRequest 절약_확정_요청
    ) {
        final String sessionId = 로그인(사용자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(절약_확정_요청)
                .when()
                .post("/profile/my-posts/{postId}/saving-confirm", 게시글_id)
                .then()
                .extract();
    }

    protected ExtractableResponse<Response> 확정_취소_요청을_보낸다(final Member 사용자, final Long 게시글_id) {
        final String sessionId = 로그인(사용자);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/profile/my-posts/{postId}/confirm-remove", 게시글_id)
                .then()
                .log().all()
                .extract();
    }

    /**
     * 신고
     */
    protected ExtractableResponse<Response> 신고를_한다(final Member member, final ReportRequest reportRequest) {
        final String sessionId = 로그인(member);

        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(reportRequest)
                .when()
                .post("/report")
                .then()
                .extract();
    }

    /**
     * 추천
     */

    protected ExtractableResponse<Response> 게시글을_추천한다(final long 게시글_id, final String sessionId) {
        return EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .put("posts/" + 게시글_id + "/up")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
