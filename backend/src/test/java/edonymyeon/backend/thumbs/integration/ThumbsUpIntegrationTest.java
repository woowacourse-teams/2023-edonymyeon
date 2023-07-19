package edonymyeon.backend.thumbs.integration;

import static io.restassured.RestAssured.given;

import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ThumbsUpIntegrationTest {

    @LocalServerPort
    private int port;

    private final PostService postService;

    private final MemberRepository memberRepository;

    private Member postWriter;

    public ThumbsUpIntegrationTest(final PostService postService, final MemberRepository memberRepository) {
        this.postService = postService;
        this.memberRepository = memberRepository;
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void 테스트_실행전_게시글을_작성하는_사람을_추가한다() {
        postWriter = new Member(
                "email",
                "password",
                "nickname",
                null
        );
        memberRepository.save(postWriter);
    }

    @Test
    void 게시글_추천기능_정상작동() {
        PostRequest postRequest = new PostRequest("title", "content", 1000L, null);
        PostResponse postResponse = postService.createPost(new MemberIdDto(postWriter.getId()), postRequest);

        Member otherMember = new Member(
                "email2",
                "password2",
                "nickname2",
                null
        );
        memberRepository.save(otherMember);

        given().log().all()
                .auth().preemptive().basic(otherMember.getEmail(), otherMember.getPassword())
                .when()
                .put("posts/" + postResponse.id() + "/up")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 로그인_되어있지_않다면_추천기능을_사용할_수_없다() {
        PostRequest postRequest = new PostRequest("title", "content", 1000L, null);
        PostResponse postResponse = postService.createPost(new MemberIdDto(1L), postRequest);

        given().log().all()
                .when()
                .put("posts/" + postResponse.id() + "/up")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
