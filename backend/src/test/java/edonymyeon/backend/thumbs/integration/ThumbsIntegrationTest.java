package edonymyeon.backend.thumbs.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ThumbsIntegrationTest extends IntegrationTest {

    public static final String UP = "up";

    public static final String DOWN = "down";

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    private Member postWriter;

    private Member otherMember;

    private Long postId;

    private Member registerMember() {
        Member member = memberTestSupport.builder()
                .build();

        memberRepository.save(member);
        return member;
    }

    @BeforeEach
    void 테스트_실행전_두_회원의_가입과_게시글_작성을_실행한다() {
        postWriter = registerMember();
        otherMember = registerMember();

        PostRequest postRequest = new PostRequest("title", "content", 1000L, null);
        PostResponse postResponse = postService.createPost(new MemberIdDto(postWriter.getId()), postRequest);

        postId = postResponse.id();
    }

    @Test
    void 게시글_추천기능_정상작동_200_OK() {
        ExtractableResponse<Response> response = requestThumbs(UP, otherMember, postId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 로그인_되어있지_않다면_추천기능을_사용할_수_없다_401_UNAUTHORIZED() {
        given().log().all()
                .when()
                .put("posts/" + postId + "/up")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 자신이_작성한_글을_추천할_수_없다_400_BAD_REQUEST() {
        ExtractableResponse<Response> response = requestThumbs(UP, postWriter, postId);

        assertSoftly(softly -> {
                    softly.assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(response.jsonPath().get("errorCode").equals(4002))
                            .isTrue();
                }
        );
    }

    @Test
    void 이미_추천한_게시글을_또_추천할_수_없다_400_BAD_REQUEST() {
        // given, when
        ExtractableResponse<Response> responseThumbsUP1 = requestThumbs(UP, otherMember, postId);
        ExtractableResponse<Response> responseThumbsUP2 = requestThumbs(UP, otherMember, postId);

        //then
        assertSoftly(softly -> {
                    softly.assertThat(responseThumbsUP1.statusCode())
                            .isEqualTo(HttpStatus.OK.value());

                    softly.assertThat(responseThumbsUP2.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(responseThumbsUP2.jsonPath().get("errorCode").equals(4000))
                            .isTrue();
                }
        );

    }

    @Test
    void 이미_비추천한_게시글을_또_비추천할_수_없다_400_BAD_REQUEST() {
        // given, when
        ExtractableResponse<Response> responseThumbsDOWN1 = requestThumbs(DOWN, otherMember, postId);
        ExtractableResponse<Response> responseThumbsDOWN2 = requestThumbs(DOWN, otherMember, postId);

        //then
        assertSoftly(softly -> {
                    softly.assertThat(responseThumbsDOWN1.statusCode())
                            .isEqualTo(HttpStatus.OK.value());

                    softly.assertThat(responseThumbsDOWN2.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(responseThumbsDOWN2.jsonPath().get("errorCode").equals(4001))
                            .isTrue();
                }
        );
    }

    @Test
    void 추천_취소_정상_작동_200_OK() {
        requestThumbs(UP, otherMember, postId);
        ExtractableResponse<Response> response = requestDeleteThumbs(UP, otherMember, postId);

        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 추천이_되어있지_않은경우_추천_취소를_할_수_없다_400_BAD_REQUEST() {
        ExtractableResponse<Response> response = requestDeleteThumbs(UP, otherMember, postId);

        assertSoftly(softly -> {
                    softly.assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(response.jsonPath().get("errorCode").equals(4003))
                            .isTrue();
                }
        );
    }

    @Test
    void 비추천인_상태일때_추천을_취소할_수_없다_400_BAD_REQUEST() {
        requestThumbs(DOWN, otherMember, postId);
        ExtractableResponse<Response> response = requestDeleteThumbs(UP, otherMember, postId);

        assertSoftly(softly -> {
                    softly.assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(response.jsonPath().get("errorCode").equals(4005))
                            .isTrue();
                }
        );
    }

    @Test
    void 비추천_취소_정상_작동() {
        requestThumbs(DOWN, otherMember, postId);
        ExtractableResponse<Response> response = requestDeleteThumbs(DOWN, otherMember, postId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 비추천이_되어있지_않은경우_비추천_취소를_할_수_없다_400_BAD_REQUEST() {
        ExtractableResponse<Response> response = requestDeleteThumbs(DOWN, otherMember, postId);

        assertSoftly(softly -> {
                    softly.assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(response.jsonPath().get("errorCode").equals(4004))
                            .isTrue();
                }
        );
    }

    @Test
    void 추천인_상태일때_비추천을_취소할_수_없다_400_BAD_REQUEST() {
        requestThumbs(UP, otherMember, postId);
        ExtractableResponse<Response> response = requestDeleteThumbs(DOWN, otherMember, postId);

        assertSoftly(softly -> {
                    softly.assertThat(response.statusCode())
                            .isEqualTo(HttpStatus.BAD_REQUEST.value());

                    softly.assertThat(response.jsonPath().get("errorCode").equals(4006))
                            .isTrue();
                }
        );
    }

    private ExtractableResponse<Response> requestThumbs(String upOrDown, Member member, Long postId) {
        return given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .put("posts/" + postId + "/" + upOrDown)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestDeleteThumbs(String upOrDown, Member member, Long postId) {
        return given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .delete("posts/" + postId + "/" + upOrDown)
                .then()
                .log().all()
                .extract();
    }
}
