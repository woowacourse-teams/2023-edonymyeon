package edonymyeon.backend.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

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

@SuppressWarnings("NonAsciiCharacters")
@IntegrationTest
public class IntegrationFixture {

    protected static final File 이미지1 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
    protected static final File 이미지2 = new File("./src/test/resources/static/img/file/test_image_2.jpg");

    @Autowired
    protected ProfileImageInfoTestSupport profileImageInfoTestSupport;

    @Autowired
    protected MemberTestSupport memberTestSupport;

    @Autowired
    protected PostTestSupport postTestSupport;

    @Autowired
    protected PostImageInfoTestSupport postImageInfoTestSupport;

    @Autowired
    protected PostIntegrationTestSupport postIntegrationTestSupport;

    @Autowired
    protected ConsumptionTestSupport consumptionTestSupport;

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
}

