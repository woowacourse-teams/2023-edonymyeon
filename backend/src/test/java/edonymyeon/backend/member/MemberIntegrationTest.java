package edonymyeon.backend.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.domain.Post;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class MemberIntegrationTest extends IntegrationTest {

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

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .get("/profile/my-posts")
                .then()
                .extract();

        final var jsonPath = response.body().jsonPath();
        final List<MyPostResponse> content = jsonPath.getList("content", MyPostResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(content.size()).isEqualTo(3);
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        });
    }
}
