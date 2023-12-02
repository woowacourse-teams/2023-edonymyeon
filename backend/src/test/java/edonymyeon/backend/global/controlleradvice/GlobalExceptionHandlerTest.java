package edonymyeon.backend.global.controlleradvice;

import static edonymyeon.backend.global.exception.ExceptionInformation.REQUEST_API_NOT_FOUND;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.support.EdonymyeonRestAssured;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class GlobalExceptionHandlerTest extends IntegrationFixture {

    @Test
    void 찾는_페이지가_없을_경우_커스텀_예외_응답을_반환한다() {
        final ExtractableResponse<Response> 예외_응답 = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .build()
                .when()
                .get("/fake")
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(예외_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                    softly.assertThat(예외_응답.body().jsonPath().getString("errorMessage"))
                            .isEqualTo(REQUEST_API_NOT_FOUND.getMessage());
                    softly.assertThat(예외_응답.body().jsonPath().getInt("errorCode"))
                            .isEqualTo(REQUEST_API_NOT_FOUND.getCode());
                }
        );
    }
}
