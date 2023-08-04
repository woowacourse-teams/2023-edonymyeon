package edonymyeon.backend.consumption.integration.steps;

import edonymyeon.backend.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
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
