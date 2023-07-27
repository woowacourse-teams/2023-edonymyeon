package edonymyeon.backend.post.integration;

import edonymyeon.backend.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
public class PostSearchIntegrationTest extends IntegrationTest {
    @Test
    void 검색어가_포함되어있지_않으면_오류가_발생한다(){
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/search")
                .then().log().all()
                .extract();

        System.out.println(response);
    }
}
