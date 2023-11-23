package edonymyeon.backend.member.integration;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.membber.auth.application.dto.LoginRequest;
import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.membber.member.application.dto.response.MyPageResponseV1_0;
import edonymyeon.backend.membber.member.application.dto.response.MyPageResponseV1_1;
import edonymyeon.backend.membber.member.domain.Member;
import edonymyeon.backend.content.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.support.EdonymyeonRestAssured;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
class MemberIntegrationTest extends IntegrationFixture {

    @Test
    void 회원_정보_V1_조회시_OK를_응답한다() {
        final Member member = memberTestSupport.builder()
                .build();

        final String sessionId = 로그인(member);

        final ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .sessionId(sessionId)
                .build()
                .when()
                .get("/profile")
                .then()
                .extract();

        final MyPageResponseV1_0 myPageResponse = response.as(MyPageResponseV1_0.class);
        assertAll(
                () -> assertThat(myPageResponse.memberId()).isEqualTo(member.getId()),
                () -> assertThat(myPageResponse.nickname()).isEqualTo(member.getNickname()),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @Test
    void 회원_정보_V1_1_조회시_OK를_응답한다() {
        final Member member = memberTestSupport.builder()
                .build();

        final String sessionId = 로그인(member);

        final ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.1.0")
                .sessionId(sessionId)
                .build()
                .when()
                .get("/profile")
                .then()
                .extract();

        final MyPageResponseV1_1 myPageResponseV110 = response.as(MyPageResponseV1_1.class);
        assertAll(
                () -> assertThat(myPageResponseV110.id()).isEqualTo(member.getId()),
                () -> assertThat(myPageResponseV110.nickname()).isEqualTo(member.getNickname()),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @Test
    void 자신이_작성한_게시글을_조회하면_OK를_응답한다() {
        final Member member = memberTestSupport.builder()
                .build();

        final Post post1 = postTestSupport.builder()
                .member(member)
                .build();
        final Post post2 = postTestSupport.builder()
                .member(member)
                .build();

        postTestSupport.builder()
                .member(member)
                .build();

        consumptionTestSupport.builder()
                .post(post1)
                .build();

        consumptionTestSupport.builder()
                .post(post2)
                .build();

        final String sessionId = 로그인(member);

        final ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .sessionId(sessionId)
                .build()
                .when()
                .get("/profile/my-posts")
                .then()
                .extract();

        final var jsonPath = response.body().jsonPath();
        final List<MyPostResponse> content = jsonPath.getList("content", MyPostResponse.class);

        assertSoftly(softly -> {
            softly.assertThat(content.size()).isEqualTo(3);
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        });
    }

    @Test
    void 삭제된_회원이_로그인시_오류발생() {
        final Member member = memberTestSupport.builder()
                .build();

        final String sessionId = 로그인(member);

        EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .sessionId(sessionId)
                .build()
                .when()
                .delete("/withdraw");

        final LoginRequest request = new LoginRequest(member.getEmail(), member.getPassword(), "werwe");

        final ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .build()
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

        final String sessionId = 로그인(member);

        EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .sessionId(sessionId)
                .build()
                .when()
                .delete("/withdraw");

        final ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .build()
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
