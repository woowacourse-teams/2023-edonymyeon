package edonymyeon.backend.post.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class PostSearchIntegrationTest extends IntegrationTest {

    @BeforeEach
    void 테스트_실행전_한_회원의_가입과_두_게시글_작성을_실행한다() {
        Member member = memberTestSupport.builder().build();

        postTestSupport.builder()
                .member(member)
                .title("애플 먹고 싶어요")
                .content("사먹어도 되나요? 자취생인데...")
                .build();

        postTestSupport.builder()
                .member(member)
                .title("사과 먹고 싶어요")
                .content("사먹어도 되나요? 자취생인데...")
                .build();
    }

    @Test
    void 검색어가_포함되어있지_않으면_오류가_발생한다_400_BasRequest(){
        ExtractableResponse<Response> response = RestAssured
                .given()
                .when()
                .get("/search")
                .then()
                .extract();

        assertSoftly(softly -> {
            softly.assertThat(response.body().jsonPath().getInt("errorCode")).isEqualTo(ExceptionInformation.REQUEST_PARAMETER_NOT_EXIST.getCode());
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        });
    }

    @Test
    void 검색어가_포함된_제목이_제대로_검색되는지_확인한다() {
        final var 검색된_게시글_조회_결과 = RestAssured
                .given()
                .when()
                .queryParam("query", "사과")
                .get("/search")
                .then()
                .extract();

        final var jsonPath = 검색된_게시글_조회_결과.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("posts")).hasSize(1);
            softly.assertThat(jsonPath.getString("posts[0].title")).contains("사과");
        });
    }

    @Test
    void 검색어가_포함된_내용이_제대로_검색되는지_확인한다() {
        final var 검색된_게시글_조회_결과 = RestAssured
                .given()
                .when()
                .queryParam("query", "자취")
                .get("/search")
                .then()
                .extract();

        final var jsonPath = 검색된_게시글_조회_결과.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("posts")).hasSize(2);
            softly.assertThat(jsonPath.getString("posts[0].content")).contains("자취");
            softly.assertThat(jsonPath.getString("posts[1].content")).contains("자취");
        });
    }

    @Test
    void 검색어에_해당하는_게시물이_없다면_빈_결과값이_나온다() {
        final var 검색된_게시글_조회_결과 = RestAssured
                .given()
                .when()
                .queryParam("query", "케로로")
                .get("/search")
                .then()
                .extract();

        final var jsonPath = 검색된_게시글_조회_결과.body().jsonPath();

        assertThat(jsonPath.getList("posts").size()).isEqualTo(0);
    }
}
