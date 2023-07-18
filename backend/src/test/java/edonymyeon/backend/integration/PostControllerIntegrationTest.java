package edonymyeon.backend.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql({"/truncate.sql", "/dummydata.sql"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private static File 이미지1 = new File("./src/test/resources/static/img/file/test_image_1.jpg");

    private static File 이미지2 = new File("./src/test/resources/static/img/file/test_image_2.jpg");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 사진_첨부_성공_테스트() {
        RestAssured.given()
                .auth().preemptive().basic("email", "password")
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("images", 이미지2, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 회원이_아니면_게시글_작성_불가_테스트() {
        RestAssured.given()
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("images", 이미지2, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 본인이_작성한_게시글_삭제_가능_테스트() {
        final ExtractableResponse<Response> 게시글_생성_요청_결과 = RestAssured.given()
                .auth().preemptive().basic("email", "password")
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("images", 이미지2, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();

        final String location = 게시글_생성_요청_결과.header("location");
        final long 게시글_id = Long.parseLong(location.split("/")[2]);

        RestAssured.given()
                .auth().preemptive().basic("email", "password")
                .when()
                .delete("/posts/" + 게시글_id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 본인이_작성하지_않은_게시글_삭제_불가능_테스트() {
        final ExtractableResponse<Response> 게시글_생성_요청_결과 = RestAssured.given()
                .auth().preemptive().basic("email", "password")
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("images", 이미지2, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract();

        final String location = 게시글_생성_요청_결과.header("location");
        final long 게시글_id = Long.parseLong(location.split("/")[2]);

        RestAssured.given()
                .auth().preemptive().basic("badman", "password")
                .when()
                .delete("/posts/" + 게시글_id)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
