package edonymyeon.backend.post.integration;

import static edonymyeon.backend.global.exception.ExceptionInformation.REQUEST_FILE_SIZE_TOO_LARGE;
import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.member.domain.Member;
import io.restassured.RestAssured;
import java.io.File;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class PostImageFileSizeIntegrationTest extends IntegrationTest {

    private static final File 크기_21MB_이미지 = new File("./src/test/resources/static/img/file/test_image_21mb.jpg");
    private static final File 크기_20MB_이미지 = new File("./src/test/resources/static/img/file/test_image_20mb.jpg");

    @Test
    void 이미지_파일_하나의_용량이_20MB_이하면_성공한다() {
        final Member 작성자 = memberTestSupport.builder().build();
        final var 게시글_생성_응답 = RestAssured.given()
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .auth().preemptive().basic(작성자.getEmail(), 작성자.getPassword())
                .when()
                .post("/posts")
                .then()
                .extract();

        assertThat(게시글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 이미지_파일_하나의_용량이_20MB를_보다_크면_실패한다() {
        final Member 작성자 = memberTestSupport.builder().build();
        final var 게시글_생성_응답 = RestAssured.given()
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("newImages", 크기_21MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .auth().preemptive().basic(작성자.getEmail(), 작성자.getPassword())
                .when()
                .post("/posts")
                .then()
                .extract();

        final ExceptionResponse 예외_응답 = 게시글_생성_응답.as(ExceptionResponse.class);
        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(게시글_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(예외_응답.errorCode()).isEqualTo(REQUEST_FILE_SIZE_TOO_LARGE.getCode());
                    assertThat(예외_응답.errorMessage()).isEqualTo(REQUEST_FILE_SIZE_TOO_LARGE.getMessage());
                }
        );
    }

    @Test
    void 요청의_크기가_200MB_이하면_성공한다() {
        final Member 작성자 = memberTestSupport.builder().build();
        final var 게시글_생성_응답 = RestAssured.given()
                .multiPart("title", "this is title")
                .multiPart("content", "this is content")
                .multiPart("price", 1000)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .multiPart("newImages", 크기_20MB_이미지, MediaType.IMAGE_JPEG_VALUE)
                .auth().preemptive().basic(작성자.getEmail(), 작성자.getPassword())
                .when()
                .post("/posts")
                .then()
                .extract();

        assertThat(게시글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /*
    max-request-size(200MB)를 초과하는 테스트를 실행할 경우
    테스트 상으로는 아예 연결이 닫혀 오류가 나기 때문에 테스트 코드를 따로 작성하지 않았습니다. (Broken pipe)

    과도하게 초과할 경우를 로컬 환경에서 포스트맨으로 요청을 날린다면 정상적으로 예외 응답을 받을 수 있습니다.
     */
}
