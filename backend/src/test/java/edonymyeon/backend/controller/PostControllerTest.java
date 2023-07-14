package edonymyeon.backend.controller;

import edonymyeon.backend.service.request.PostRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 제목이_30자_초과면_예외가_발생한다() {
        final PostRequest request = new PostRequest("이리내이리내이리내이리내이리내이리내이리내이리내이리내이리내이", "바보", 10L);
        RestAssured.given().contentType(MediaType.APPLICATION_JSON_VALUE).body(request).when().post("/posts").then()
                .statusCode(HttpStatus.BAD_REQUEST.value()).log().all();
    }
}
