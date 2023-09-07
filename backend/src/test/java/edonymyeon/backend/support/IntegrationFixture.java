package edonymyeon.backend.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.application.NotificationSender;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
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
    private NotificationSender notificationSender;

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

    protected long 응답의_location헤더에서_id를_추출한다(final ExtractableResponse<Response> 게시글_생성_요청_결과) {
        final String location = 게시글_생성_요청_결과.header("location");
        final String[] split = location.split("/");
        return Long.parseLong(split[split.length - 1]);
    }

    protected ExtractableResponse<Response> 게시글_하나를_상세_조회한다(final Member 열람인, final long 게시글_id) {
        return RestAssured
                .given()
                .when()
                .auth().preemptive().basic(열람인.getEmail(), 열람인.getPassword())
                .get("/posts/" + 게시글_id)
                .then()
                .extract();
    }

    private void 알림전송기능을_모킹한다() {
        doNothing().when(notificationSender).sendNotification(any(), any());
    }

    public class CommentSteps {

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

        public static ExtractableResponse<Response> 게시물에_대한_댓글을_모두_조회한다(
                final Long 게시글_id,
                final Member 사용자
        ) {
            return RestAssured.given()
                    .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                    .when()
                    .get("/posts/{postId}/comments", 게시글_id)
                    .then()
                    .extract();
        }
    }

    public class ConsumptionSteps {

        public static ExtractableResponse<Response> 특정_기간의_소비금액을_확인한다(final Integer 기간, final Member 사용자) {
            final ExtractableResponse<Response> 조회_응답 = RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                    .when()
                    .get("/consumptions?period-month={periodMonth}", 기간)
                    .then()
                    .extract();
            return 조회_응답;
        }
    }

    public class MemberConsumptionSteps {

        public static ExtractableResponse<Response> 구매_확정_요청을_보낸다(
                final Member 사용자,
                final Long 게시글_id,
                final PurchaseConfirmRequest 구매_확정_요청
        ) {
            return RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(구매_확정_요청)
                    .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                    .when()
                    .post("/profile/my-posts/{postId}/purchase-confirm", 게시글_id)
                    .then()
                    .extract();
        }

        public static ExtractableResponse<Response> 절약_확정_요청을_보낸다(
                final Member 사용자,
                final Long 게시글_id,
                final SavingConfirmRequest 절약_확정_요청
        ) {
            return RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(절약_확정_요청)
                    .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                    .when()
                    .post("/profile/my-posts/{postId}/saving-confirm", 게시글_id)
                    .then()
                    .extract();
        }

        public static ExtractableResponse<Response> 확정_취소_요청을_보낸다(final Member 사용자, final Long 게시글_id) {
            return RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                    .when()
                    .delete("/profile/my-posts/{postId}/confirm-remove", 게시글_id)
                    .then()
                    .log().all()
                    .extract();
        }
    }
}

