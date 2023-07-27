package edonymyeon.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class AuthIntegrationTest extends IntegrationTest {

    @Test
    public void 이메일_중복을_체크한다_중복X() {
        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "email")
                .param("value", "email@naver.com")
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isTrue();
                }
        );
    }

    @Test
    public void 이메일_중복을_체크한다_중복O() {
        final Member member = memberTestSupport.builder()
                .email("email@naver.com")
                .build();
        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "email")
                .param("value", member.getEmail())
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isFalse();
                }
        );
    }

    @Test
    public void 닉네임_중복을_체크한다_중복X() {
        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "nickname")
                .param("value", "2rinebabo")
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isTrue();
                }
        );
    }

    @Test
    public void 닉네임_중복을_체크한다_중복O() {
        final Member member = memberTestSupport.builder()
                .nickname("2rinebabo")
                .build();

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "nickname")
                .param("value", member.getNickname())
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isFalse();
                }
        );
    }

    @Test
    public void 회원가입_성공() {
        final JoinRequest request = new JoinRequest("email@naver.com", "password123!", "kerrobabo");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/join")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void 실제_회원가입_요청시_이메일_중복() {
        final String duplicatedEmail = "email";

        memberTestSupport.builder()
                .email(duplicatedEmail)
                .build();

        final JoinRequest request = new JoinRequest(duplicatedEmail, "password123!", "kerrobabo");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/join")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void 실제_회원가입_요청시_닉네임_중복() {
        final String duplicatedNickname = "nickname";

        memberTestSupport.builder()
                .nickname(duplicatedNickname)
                .build();

        final JoinRequest request = new JoinRequest("foxbabo", "password123!", duplicatedNickname);

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/join")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}