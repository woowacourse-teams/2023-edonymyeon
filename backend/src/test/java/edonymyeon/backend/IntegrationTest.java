package edonymyeon.backend;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.support.ConsumptionTestSupport;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostImageInfoTestSupport;
import edonymyeon.backend.support.PostIntegrationTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.support.ProfileImageInfoTestSupport;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@DisplayNameGeneration(ReplaceUnderscores.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

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

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
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
        return Long.parseLong(location.split("/")[2]);
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
}

