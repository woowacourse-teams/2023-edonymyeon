package edonymyeon.backend.post.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import io.restassured.RestAssured;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class PostSearchIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostService postService;

    @BeforeEach
    void 테스트_실행전_한_회원의_가입과_두_게시글_작성을_실행한다() {
        Member member = memberTestSupport.builder().build();
        memberRepository.save(member);

        writePost(member, "애플 먹고 싶어요", "사먹어도 되나요? 자취생인데...", 14_000L);
        writePost(member, "사과 먹고 싶어요", "사먹어도 되나요? 자취생인데...", 14_000L);
    }

    @Test
    void 검색어가_포함되어있지_않으면_오류가_발생한다_400_BasRequest(){
        RestAssured
                .given().log().all()
                .when()
                .get("/search")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
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

    private void writePost(Member member,String title, String content, Long price) {
        PostRequest postRequest = new PostRequest(
                title,
                content,
                price,
                Collections.emptyList()
        );
        postService.createPost(new MemberIdDto(member.getId()), postRequest);
    }
}
