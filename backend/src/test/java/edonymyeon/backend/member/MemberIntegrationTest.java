package edonymyeon.backend.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.member.application.dto.MyPageResponse;
import edonymyeon.backend.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
public class MemberIntegrationTest extends IntegrationTest {

    @Autowired
    EntityManager entityManager;

    @Test
    void 회원_정보_조회시_OK를_응답한다() {
        final Member member = memberTestSupport.builder()
                .email("email")
                .password("password")
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
                () -> assertThat(myPageResponse.nickname()).isEqualTo(member.getNickname())
        );
    }
}
