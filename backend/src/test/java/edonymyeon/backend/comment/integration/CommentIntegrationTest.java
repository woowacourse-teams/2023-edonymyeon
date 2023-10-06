package edonymyeon.backend.comment.integration;

import static edonymyeon.backend.global.exception.ExceptionInformation.COMMENT_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.COMMENT_MEMBER_NOT_SAME;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.image.application.ImageType;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@Import(TestConfig.class)
public class CommentIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    @Value("${image.domain}")
    private String domain;

    @Test
    void 이미지가_포함된_댓글을_작성한다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();

        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);

        assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 이미지가_포함되지_않은_댓글을_작성한다() throws IOException {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();

        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지없이(게시글.getId(), "댓글이다", 댓글_작성자);

        assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 유효하지_않은_게시글에_댓글을_달_수_없다() {
        final Member 댓글_작성자 = memberTestSupport.builder().build();

        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지포함(-1L, 이미지, "댓글이다", 댓글_작성자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(댓글_생성_응답.jsonPath().getInt("errorCode")).isEqualTo(POST_ID_NOT_FOUND.getCode());
                    assertThat(댓글_생성_응답.jsonPath().getString("errorMessage")).isEqualTo(POST_ID_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    void 댓글_작성자가_댓글을_삭제한다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);

        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글_생성_응답);
        final ExtractableResponse<Response> 댓글_삭제_응답 = 댓글을_삭제한다(게시글.getId(), 댓글_id, 댓글_작성자);

        assertThat(댓글_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 댓글_작성자가_아닌_사람은_댓글을_삭제할_수_없다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);
        final Member 수상한_사람 = memberTestSupport.builder().build();

        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글_생성_응답);
        final ExtractableResponse<Response> 댓글_삭제_응답 = 댓글을_삭제한다(게시글.getId(), 댓글_id, 수상한_사람);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_삭제_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                    assertThat(댓글_삭제_응답.jsonPath().getInt("errorCode")).isEqualTo(COMMENT_MEMBER_NOT_SAME.getCode());
                    assertThat(댓글_삭제_응답.jsonPath().getString("errorMessage")).isEqualTo(
                            COMMENT_MEMBER_NOT_SAME.getMessage());
                }
        );
    }

    @Test
    void path_variable의_게시글_id가_삭제하려는_댓글이_속한_게시글의_id와_같지_않으면_댓글을_삭제할_수_없다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "댓글이다", 댓글_작성자);

        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글_생성_응답);
        final ExtractableResponse<Response> 댓글_삭제_응답 = 댓글을_삭제한다(게시글.getId() + 1, 댓글_id, 댓글_작성자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_삭제_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(댓글_삭제_응답.jsonPath().getInt("errorCode")).isEqualTo(COMMENT_ID_NOT_FOUND.getCode());
                    assertThat(댓글_삭제_응답.jsonPath().getString("errorMessage")).isEqualTo(
                            COMMENT_ID_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    void 로그인하지_않아도_댓글을_조회할_수_있다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글1 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "this is comment1", 댓글_작성자);
        final ExtractableResponse<Response> 댓글2 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "this is comment2", 댓글_작성자);

        final long 댓글1_id = 응답의_location헤더에서_id를_추출한다(댓글1);
        final long 댓글2_id = 응답의_location헤더에서_id를_추출한다(댓글2);

        final ExtractableResponse<Response> 댓글_조회_응답 = 게시물에_대한_댓글을_모두_조회한다(게시글.getId());

        Pattern 이미지_형식 = Pattern.compile(domain + ImageType.COMMENT.getSaveDirectory() + "test-inserting\\d+\\.(png|jpg)");

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(댓글_조회_응답.jsonPath().getList("comments")).hasSize(2);
                    assertThat(댓글_조회_응답.jsonPath().getInt("commentCount")).isEqualTo(2);

                    assertThat(댓글_조회_응답.jsonPath().getInt("comments[0].id")).isEqualTo(댓글1_id);
                    assertThat(이미지_형식.matcher(댓글_조회_응답.jsonPath().getString("comments[0].image")).matches()).isTrue();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].comment")).isEqualTo("this is comment1");
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].createdAt")).isNotBlank();
                    assertThat(댓글_조회_응답.jsonPath().getBoolean("comments[0].isWriter")).isFalse();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].writer.nickname")).isEqualTo(
                            댓글_작성자.getNickname());

                    assertThat(댓글_조회_응답.jsonPath().getInt("comments[1].id")).isEqualTo(댓글2_id);
                    assertThat(이미지_형식.matcher(댓글_조회_응답.jsonPath().getString("comments[1].image")).matches()).isTrue();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[1].comment")).isEqualTo("this is comment2");
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[1].createdAt")).isNotBlank();
                    assertThat(댓글_조회_응답.jsonPath().getBoolean("comments[1].isWriter")).isFalse();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[1].writer.nickname")).isEqualTo(
                            댓글_작성자.getNickname());
                }
        );
    }

    @Test
    void 댓글_작성자가_댓글을_조회하면_isWriter_값은_true다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글1 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "this is comment1", 댓글_작성자);
        final ExtractableResponse<Response> 댓글2 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "this is comment2", 댓글_작성자);

        final long 댓글1_id = 응답의_location헤더에서_id를_추출한다(댓글1);
        final long 댓글2_id = 응답의_location헤더에서_id를_추출한다(댓글2);

        final ExtractableResponse<Response> 댓글_조회_응답 = 게시물에_대한_댓글을_모두_조회한다(게시글.getId(), 댓글_작성자);

        Pattern 이미지_형식 = Pattern.compile(domain + ImageType.COMMENT.getSaveDirectory() + "test-inserting\\d+\\.(png|jpg)");

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(댓글_조회_응답.jsonPath().getList("comments")).hasSize(2);
                    assertThat(댓글_조회_응답.jsonPath().getInt("commentCount")).isEqualTo(2);

                    assertThat(댓글_조회_응답.jsonPath().getInt("comments[0].id")).isEqualTo(댓글1_id);
                    assertThat(이미지_형식.matcher(댓글_조회_응답.jsonPath().getString("comments[0].image")).matches()).isTrue();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].comment")).isEqualTo("this is comment1");
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].createdAt")).isNotBlank();
                    assertThat(댓글_조회_응답.jsonPath().getBoolean("comments[1].isWriter")).isTrue();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].writer.nickname")).isEqualTo(
                            댓글_작성자.getNickname());

                    assertThat(댓글_조회_응답.jsonPath().getInt("comments[1].id")).isEqualTo(댓글2_id);
                    assertThat(이미지_형식.matcher(댓글_조회_응답.jsonPath().getString("comments[1].image")).matches()).isTrue();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[1].comment")).isEqualTo("this is comment2");
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[1].createdAt")).isNotBlank();
                    assertThat(댓글_조회_응답.jsonPath().getBoolean("comments[1].isWriter")).isTrue();
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[1].writer.nickname")).isEqualTo(
                            댓글_작성자.getNickname());
                }
        );
    }

    @Test
    void 삭제된_댓글은_조회되지_않는다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");

        final ExtractableResponse<Response> 댓글 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, "this is comment1", 댓글_작성자);
        final long 댓글_id = 응답의_location헤더에서_id를_추출한다(댓글);
        댓글을_삭제한다(게시글.getId(), 댓글_id, 댓글_작성자);
        final ExtractableResponse<Response> 댓글_조회_응답 = 게시물에_대한_댓글을_모두_조회한다(게시글.getId(), 댓글_작성자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(댓글_조회_응답.jsonPath().getList("comments")).isEmpty();
                }
        );
    }

    @Test
    void 이미지가_없으면_image_값은_null_로_조회된다() {
        final Post 게시글 = postTestSupport.builder().build();
        commentTestSupport.builder().post(게시글).build();
        final Member 사용자 = memberTestSupport.builder().build();

        final ExtractableResponse<Response> 댓글_조회_응답 = 게시물에_대한_댓글을_모두_조회한다(게시글.getId(), 사용자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(댓글_조회_응답.jsonPath().getList("comments")).hasSize(1);
                    assertThat(댓글_조회_응답.jsonPath().getString("comments[0].image")).isNull();
                }
        );
    }

    @Test
    void 댓글이_없으면_빈_리스트가_조회된다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 사용자 = memberTestSupport.builder().build();

        final ExtractableResponse<Response> 댓글_조회_응답 = 게시물에_대한_댓글을_모두_조회한다(게시글.getId(), 사용자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(댓글_조회_응답.jsonPath().getList("comments")).isEmpty();
                }
        );
    }

    @Test
    void 게시글이_존재하지_않으면_예외가_발생한다() {
        final Member 사용자 = memberTestSupport.builder().build();

        final ExtractableResponse<Response> 댓글_조회_응답 = 게시물에_대한_댓글을_모두_조회한다(-1L, 사용자);

        assertSoftly(
                softAssertions -> {
                    assertThat(댓글_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(댓글_조회_응답.jsonPath().getInt("errorCode")).isEqualTo(POST_ID_NOT_FOUND.getCode());
                    assertThat(댓글_조회_응답.jsonPath().getString("errorMessage")).isEqualTo(POST_ID_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    void 엄청_긴_댓글을_작성한다() {
        final Post 게시글 = postTestSupport.builder().build();
        final Member 댓글_작성자 = memberTestSupport.builder().build();
        String 엄청긴내용 = "https://www.google.com/search?q=%EA%B5%AD%EB%B0%A5&sca_esv=563011930&sxsrf=AB5stBiDy2a0MwlQmu6VB-8YpfjsEyjxEQ:1693986737909&tbm=isch&source=iu&ictx=1&vet=1&fir=sIvtHEk9_U_o5M%252CpD1Tq-sHZpN7AM%252C%252Fg%252F12392002%253B4HF-UXGBj8FhYM%252CE9uWSBBha3D_gM%252C_%253BJYaErMf7-y-c_M%252CrSd-S_vLxb3sGM%252C_%253BpkfPdVq-V8ZlSM%252CMMk5hcmb_gk_3M%252C_%253BdzDN-T5gf8f6bM%252CP3XbliiGuxt2fM%252C_%253BJNdLHh8L8dOAEM%252CGCZOxBwC4UaqpM%252C_&usg=AI4_-kQ5oFr6wN3omZLxhEDp75ruIzRELQ&sa=X&sqi=2&ved=2ahUKEwjSrpGuwJWBAxWI-2EKHaIzD84Q_B16BAhHEAE#imgrc=sIvtHEk9_U_o5M";

        final File 이미지 = new File("./src/test/resources/static/img/file/test_image_1.jpg");
        final ExtractableResponse<Response> 댓글_생성_응답 = 댓글을_생성한다_이미지포함(게시글.getId(), 이미지, 엄청긴내용, 댓글_작성자);

        assertThat(댓글_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
