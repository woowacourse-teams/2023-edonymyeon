package edonymyeon.backend.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
public class PostIntegrationTest extends IntegrationTest {

    @LocalServerPort
    private int port;

    private static File 이미지1 = new File("./src/test/resources/static/img/file/test_image_1.jpg");

    private static File 이미지2 = new File("./src/test/resources/static/img/file/test_image_2.jpg");

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 사진_첨부_성공_테스트() {
        final Member member = 사용자를_하나_만든다();
        final var 게시글_생성_결과 = 게시글을_하나_만든다(member);

        assertThat(게시글_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 회원이_아니면_게시글_작성_불가_테스트() {
        final var response = RestAssured.given()
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("images", 이미지2, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 본인이_작성한_게시글_삭제_가능_테스트() {
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_결과 = 게시글을_하나_만든다(member);

        final String location = 게시글_생성_결과.header("location");
        final long 게시글_id = Long.parseLong(location.split("/")[2]);

        RestAssured.given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .when()
                .delete("/posts/" + 게시글_id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 본인이_작성하지_않은_게시글_삭제_불가능_테스트() {
        final Member member = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_요청_결과 = 게시글을_하나_만든다(member);

        final String location = 게시글_생성_요청_결과.header("location");
        final long 게시글_id = Long.parseLong(location.split("/")[2]);

        final Member otherMember = memberTestSupport.builder()
                .email("other")
                .password("password")
                .build();

        RestAssured.given()
                .auth().preemptive().basic(otherMember.getEmail(), otherMember.getPassword())
                .when()
                .delete("/posts/" + 게시글_id)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    private Member 사용자를_하나_만든다() {
        return memberTestSupport.builder()
                .email("email")
                .password("password")
                .build();
    }

    private static ExtractableResponse<Response> 게시글을_하나_만든다(final Member member) {
        return RestAssured.given()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("images", 이미지2, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .extract();
    }

    @Test
    void 기본조건으로_전체게시글_조회(@Autowired PostService postService) {
        한_사용자가_게시글을_서른개_작성한다(postService);

        final var 게시글_전체_조회_결과 = RestAssured
                .when()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_결과.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("posts")).hasSize(20);
            softly.assertThat(jsonPath.getLong("posts[0].id")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].title")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].image")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].content")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].writer.nickName")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].createdAt")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.viewCount")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.commentCount")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.scrapCount")).isNotNull();
        });
    }

    @Test
    void 한_번에_3개씩_조회하는_조건으로_전체게시글_조회(@Autowired PostService postService) {
        한_사용자가_게시글을_서른개_작성한다(postService);

        final var 게시글_전체_조회_결과 = RestAssured
                .given()
                .param("size", 3)
                .when()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_결과.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("posts")).hasSize(3);
            softly.assertThat(jsonPath.getLong("posts[0].id")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].title")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].image")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].content")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].writer.nickName")).isNotNull();
            softly.assertThat(jsonPath.getString("posts[0].createdAt")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.viewCount")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.commentCount")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.scrapCount")).isNotNull();
        });
    }

    @Test
    void 한_번에_3개씩_조회하는_조건으로_2페이지_전체게시글_조회(@Autowired PostService postService) {
        한_사용자가_게시글을_서른개_작성한다(postService);

        final var 게시글_전체_조회_결과 = RestAssured
                .given()
                .param("size", 3)
                .param("page", 1)
                .when()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_결과.body().jsonPath();

        final var 전체조회_4번째_게시글 = postService.findAllPost(
                        GeneralFindingCondition.builder()
                                .size(3)
                                .page(1)
                                .build())
                .get(0);

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("posts")).hasSize(3);
            softly.assertThat(jsonPath.getLong("posts[0].id")).isEqualTo(전체조회_4번째_게시글.id());
            softly.assertThat(jsonPath.getString("posts[0].title")).isEqualTo(전체조회_4번째_게시글.title());
            softly.assertThat(jsonPath.getString("posts[0].image")).isEqualTo(전체조회_4번째_게시글.image());
            softly.assertThat(jsonPath.getString("posts[0].content")).isEqualTo(전체조회_4번째_게시글.content());
            softly.assertThat(jsonPath.getString("posts[0].writer.nickName")).isEqualTo(전체조회_4번째_게시글.writer().nickName());
            softly.assertThat(jsonPath.getString("posts[0].createdAt")).isNotNull();
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.viewCount")).isEqualTo(전체조회_4번째_게시글.reactionCount().viewCount());
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.commentCount")).isEqualTo(전체조회_4번째_게시글.reactionCount().commentCount());
            softly.assertThat(jsonPath.getInt("posts[0].reactionCount.scrapCount")).isEqualTo(전체조회_4번째_게시글.reactionCount().scrapCount());
        });
    }

    private void 한_사용자가_게시글을_서른개_작성한다(PostService postService) {
        final Member member = 사용자를_하나_만든다();
        for (int i = 0; i < 30; i++) {
            try {
                final PostRequest postRequest = getPostRequest();
                postService.createPost(new MemberIdDto(member.getId()), postRequest);
            } catch (IOException e) {
                fail(e);
            }
        }
    }

    @Test
    void 로그인하지_않은_사용자가_게시글_하나를_상세조회하는_경우(@Autowired PostService postService) throws IOException {
        final Member member = 사용자를_하나_만든다();
        final PostRequest postRequest = getPostRequest();
        final PostResponse postResponse = postService.createPost(new MemberIdDto(member.getId()), postRequest);

        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/posts/" + postResponse.id())
                .then().log().all()
                .extract();

        final SpecificPostInfoResponse post = postService.findSpecificPost(postResponse.id(),
                new MemberIdDto(0L));

        assertThat(response.statusCode()).isEqualTo(200);
        assertAll(
                () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(post.id()),
                () -> assertThat(response.body().jsonPath().getString("title")).isEqualTo(post.title()),
                () -> assertThat(response.body().jsonPath().getLong("price")).isEqualTo(post.price()),
                () -> assertThat(response.body().jsonPath().getString("content")).isEqualTo(post.content()),

                () -> assertThat(response.body().jsonPath().getList("images")).hasSize(2),
                () -> assertThat(response.body().jsonPath().getString("images[0]")).isEqualTo(post.images().get(0)),
                () -> assertThat(response.body().jsonPath().getString("images[1]")).isEqualTo(post.images().get(1)),

                () -> assertThat(response.body().jsonPath().getLong("writer.id")).isEqualTo(
                        post.writer().id()),
                () -> assertThat(response.body().jsonPath().getString("writer.nickname")).isEqualTo(
                        post.writer().nickname()),
                () -> assertThat(response.body().jsonPath().getString("writer.profileImage")).isEqualTo(
                        post.writer().profileImage()),

                () -> assertThat(response.body().jsonPath().getInt("reactionCount.viewCount")).isEqualTo(
                        post.reactionCount().viewCount()),
                () -> assertThat(response.body().jsonPath().getInt("reactionCount.commentCount")).isEqualTo(
                        post.reactionCount().commentCount()),
                () -> assertThat(response.body().jsonPath().getInt("reactionCount.scrapCount")).isEqualTo(
                        post.reactionCount().scrapCount()),

                () -> assertThat(response.body().jsonPath().getInt("upCount")).isEqualTo(post.upCount()),
                () -> assertThat(response.body().jsonPath().getInt("downCount")).isEqualTo(post.downCount()),
                () -> assertThat(response.body().jsonPath().getBoolean("isUp")).isEqualTo(post.isUp()),
                () -> assertThat(response.body().jsonPath().getBoolean("isDown")).isEqualTo(post.isDown()),
                () -> assertThat(response.body().jsonPath().getBoolean("isScrap")).isEqualTo(post.isScrap()),
                () -> assertThat(response.body().jsonPath().getBoolean("isWriter")).isEqualTo(false)
        );
    }

    @Test
    void 로그인한_사용자가_자신의_게시글_하나를_상세조회하는_경우(@Autowired PostService postService) throws IOException {
        final Member member = 사용자를_하나_만든다();
        final PostRequest postRequest = getPostRequest();
        final PostResponse postResponse = postService.createPost(new MemberIdDto(member.getId()), postRequest);

        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .auth().preemptive().basic("email", "password")
                .get("/posts/" + postResponse.id())
                .then().log().all()
                .extract();

        final SpecificPostInfoResponse post = postService.findSpecificPost(postResponse.id(),
                new MemberIdDto(member.getId()));

        assertThat(response.statusCode()).isEqualTo(200);
        assertAll(
                () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(post.id()),
                () -> assertThat(response.body().jsonPath().getString("title")).isEqualTo(post.title()),
                () -> assertThat(response.body().jsonPath().getLong("price")).isEqualTo(post.price()),
                () -> assertThat(response.body().jsonPath().getString("content")).isEqualTo(post.content()),

                () -> assertThat(response.body().jsonPath().getList("images")).hasSize(2),
                () -> assertThat(response.body().jsonPath().getString("images[0]")).isEqualTo(post.images().get(0)),
                () -> assertThat(response.body().jsonPath().getString("images[1]")).isEqualTo(post.images().get(1)),

                () -> assertThat(response.body().jsonPath().getLong("writer.id")).isEqualTo(
                        post.writer().id()),
                () -> assertThat(response.body().jsonPath().getString("writer.nickname")).isEqualTo(
                        post.writer().nickname()),
                () -> assertThat(response.body().jsonPath().getString("writer.profileImage")).isEqualTo(
                        post.writer().profileImage()),

                () -> assertThat(response.body().jsonPath().getInt("reactionCount.viewCount")).isEqualTo(
                        post.reactionCount().viewCount()),
                () -> assertThat(response.body().jsonPath().getInt("reactionCount.commentCount")).isEqualTo(
                        post.reactionCount().commentCount()),
                () -> assertThat(response.body().jsonPath().getInt("reactionCount.scrapCount")).isEqualTo(
                        post.reactionCount().scrapCount()),

                () -> assertThat(response.body().jsonPath().getInt("upCount")).isEqualTo(post.upCount()),
                () -> assertThat(response.body().jsonPath().getInt("downCount")).isEqualTo(post.downCount()),
                () -> assertThat(response.body().jsonPath().getBoolean("isUp")).isEqualTo(post.isUp()),
                () -> assertThat(response.body().jsonPath().getBoolean("isDown")).isEqualTo(post.isDown()),
                () -> assertThat(response.body().jsonPath().getBoolean("isScrap")).isEqualTo(post.isScrap()),
                () -> assertThat(response.body().jsonPath().getBoolean("isWriter")).isEqualTo(true)
        );
    }

    @Test
    void 로그인한_사용자가_타인의_게시글_하나를_상세조회하는_경우(@Autowired PostService postService) throws IOException {
        final Member member = 사용자를_하나_만든다();
        final Member member2 = memberTestSupport.builder()
                .email("email1")
                .password("password")
                .build();

        final PostRequest postRequest = getPostRequest();
        final PostResponse postResponse = postService.createPost(new MemberIdDto(member.getId()), postRequest);

        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .auth().preemptive().basic("email1", "password")
                .get("/posts/" + postResponse.id())
                .then().log().all()
                .extract();

        final SpecificPostInfoResponse post = postService.findSpecificPost(postResponse.id(),
                new MemberIdDto(member2.getId()));

        assertThat(response.statusCode()).isEqualTo(200);
        assertAll(
                () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(post.id()),
                () -> assertThat(response.body().jsonPath().getString("title")).isEqualTo(post.title()),
                () -> assertThat(response.body().jsonPath().getLong("price")).isEqualTo(post.price()),
                () -> assertThat(response.body().jsonPath().getString("content")).isEqualTo(post.content()),

                () -> assertThat(response.body().jsonPath().getList("images")).hasSize(2),
                () -> assertThat(response.body().jsonPath().getString("images[0]")).isEqualTo(post.images().get(0)),
                () -> assertThat(response.body().jsonPath().getString("images[1]")).isEqualTo(post.images().get(1)),

                () -> assertThat(response.body().jsonPath().getLong("writer.id")).isEqualTo(
                        post.writer().id()),
                () -> assertThat(response.body().jsonPath().getString("writer.nickname")).isEqualTo(
                        post.writer().nickname()),
                () -> assertThat(response.body().jsonPath().getString("writer.profileImage")).isEqualTo(
                        post.writer().profileImage()),

                () -> assertThat(response.body().jsonPath().getInt("reactionCount.viewCount")).isEqualTo(
                        post.reactionCount().viewCount()),
                () -> assertThat(response.body().jsonPath().getInt("reactionCount.commentCount")).isEqualTo(
                        post.reactionCount().commentCount()),
                () -> assertThat(response.body().jsonPath().getInt("reactionCount.scrapCount")).isEqualTo(
                        post.reactionCount().scrapCount()),

                () -> assertThat(response.body().jsonPath().getInt("upCount")).isEqualTo(post.upCount()),
                () -> assertThat(response.body().jsonPath().getInt("downCount")).isEqualTo(post.downCount()),
                () -> assertThat(response.body().jsonPath().getBoolean("isUp")).isEqualTo(post.isUp()),
                () -> assertThat(response.body().jsonPath().getBoolean("isDown")).isEqualTo(post.isDown()),
                () -> assertThat(response.body().jsonPath().getBoolean("isScrap")).isEqualTo(post.isScrap()),
                () -> assertThat(response.body().jsonPath().getBoolean("isWriter")).isEqualTo(false)
        );
    }

    private PostRequest getPostRequest() throws IOException {
        final InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        final MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        final InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        final MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        final List<MultipartFile> multipartFiles = List.of(file1, file2);

        return new PostRequest(
                "I love you",
                "He wisely contented himself with his family and his love of nature.",
                13_000L,
                multipartFiles
        );
    }
}
