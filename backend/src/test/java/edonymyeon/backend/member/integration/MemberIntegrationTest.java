package edonymyeon.backend.member.integration;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class MemberIntegrationTest extends IntegrationFixture {

    @Test
    void 회원_정보_조회시_OK를_응답한다() {
        final Member member = memberTestSupport.builder()
                .build();

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .get("/profile")
                .then()
                .extract();

        final MyPageResponse myPageResponse = response.as(MyPageResponse.class);
        assertAll(
                () -> assertThat(myPageResponse.memberId()).isEqualTo(member.getId()),
                () -> assertThat(myPageResponse.nickname()).isEqualTo(member.getNickname()),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @Test
    void 삭제된_회원이_로그인시_오류발생() {
        final Member member = memberTestSupport.builder()
                .build();

        RestAssured
                .given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .delete("/withdraw");

        final LoginRequest request = new LoginRequest(member.getEmail(), member.getPassword(), "werwe");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/login")
                .then()
                .extract();
        final ExceptionResponse exception = response.body()
                .as(ExceptionResponse.class);

        assertSoftly(
                softly -> {
                    softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                    softly.assertThat(exception.errorCode()).isEqualTo(MEMBER_IS_DELETED.getCode());
                    softly.assertThat(exception.errorMessage()).isEqualTo(MEMBER_IS_DELETED.getMessage());
                }
        );
    }

    @Test
    void 삭제한_회원의_게시글을_조회한다() {
        final Member member = memberTestSupport.builder()
                .build();

        final Post post = postTestSupport.builder()
                .member(member)
                .build();

        RestAssured
                .given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .delete("/withdraw");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .when()
                .get("/posts/" + post.getId())
                .then()
                .extract();

        final JsonPath jsonPath = response.body()
                .jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(jsonPath.getString("writer.nickname")).isEqualTo("Unknown");
        });
    }
}
