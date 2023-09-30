package edonymyeon.backend.post.integration;

import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;
import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_DOMAIN_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_STORE_NAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_IMAGE_COUNT_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_NOT_SAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.PostReadService;
import edonymyeon.backend.post.application.dto.response.SpecificPostInfoResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.support.EdonymyeonRestAssured;
import edonymyeon.backend.support.EdonymyeonRestAssured.EdonymyeonRestAssuredBuilder;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class PostIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    @Value("${domain}")
    private String domain;

    @Test
    void 사진을_첨부해서_게시글_작성_가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final var 게시글_생성_결과 = 게시글을_하나_만든다(작성자);

        assertThat(게시글_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 회원이_아니면_게시글_작성_불가능하다() {
        final var response = EdonymyeonRestAssured.builder()
                .version(1)
                .build()
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
    void 게시글을_작성할_때_내용은_0자_이상_가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final String sessionId = 로그인(작성자);
        final var response = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "this is title")
                .multiPart("content", "")
                .multiPart("price", 1000)
                .multiPart("images", 이미지1, MediaType.IMAGE_JPEG_VALUE)
                .when()
                .post("/posts")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 게시글을_작성할때_이미지가_10개_이상이면_예외가_발생한다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final String sessionId = 로그인(작성자);

        final ExtractableResponse<Response> 게시글_작성_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "제목")
                .multiPart("content", "내용")
                .multiPart("price", 10000)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .when()
                .post("/posts")
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(게시글_작성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    softly.assertThat(게시글_작성_응답.jsonPath().getInt("errorCode")).isEqualTo(POST_IMAGE_COUNT_INVALID.getCode());
                    softly.assertThat(게시글_작성_응답.jsonPath().getString("errorMessage"))
                            .isEqualTo(POST_IMAGE_COUNT_INVALID.getMessage());
                }
        );
    }

    @Test
    void 본인이_작성한_게시글은_삭제_가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        게시글을_삭제한다(작성자, 게시글_id);
    }

    @Test
    void 본인이_작성하지_않은_게시글은_삭제_불가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final Member 작성자가_아닌_사람 = 사용자를_하나_만든다();
        final String sessionId = 로그인(작성자가_아닌_사람);
        EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .when()
                .delete("/posts/" + 게시글_id)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void 게시글이_추천이_되어있다면_게시글_삭제시_추천도_삭제된다(@Autowired ThumbsRepository thumbsRepository) {
        //given
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);
        final Member 추천하는_사람 = 사용자를_하나_만든다();

        final String sessionId1 = 로그인(추천하는_사람);

        게시글을_추천한다(게시글_id, sessionId1);

        //when
        final ExtractableResponse<Response> 게시글_삭제_응답 = 게시글을_삭제한다(작성자, 게시글_id);

        //then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsRepository.findByPostId(게시글_id)).isEmpty();
                    softly.assertThat(게시글_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }
        );
    }

    @Test
    void 게시글이_소비_확정_되어있다면_게시글_삭제시_소비_확정_내역도_삭제된다(@Autowired ConsumptionRepository consumptionRepository) {
        //given
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);
        final PurchaseConfirmRequest 구매_확정_요청 = new PurchaseConfirmRequest(10000L, 2023, 7);
        구매_확정_요청을_보낸다(작성자, 게시글_id, 구매_확정_요청);

        //when
        final ExtractableResponse<Response> 게시글_삭제_응답 = 게시글을_삭제한다(작성자, 게시글_id);

        //then
        assertSoftly(softly -> {
                    softly.assertThat(consumptionRepository.findByPostId(게시글_id)).isEmpty();
                    softly.assertThat(게시글_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
                }
        );
    }

    @Test
    void 게시글에_신고내역이_존재해도_게시글을_삭제할_수_있다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final Member 신고자 = 사용자를_하나_만든다();
        final ReportRequest reportRequest = new ReportRequest("POST", 게시글_id, 4, null);
        신고를_한다(신고자, reportRequest);

        final ExtractableResponse<Response> 게시글_삭제_응답 = 게시글을_삭제한다(작성자, 게시글_id);

        assertThat(게시글_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 게시글에_댓글이_존재해도_게시글을_삭제할_수_있다() throws IOException {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final Member 댓글_쓰는_자 = 사용자를_하나_만든다();
        댓글을_생성한다_이미지없이(게시글_id, "내용", 댓글_쓰는_자);

        final ExtractableResponse<Response> 게시글_삭제_응답 = 게시글을_삭제한다(작성자, 게시글_id);

        assertThat(게시글_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 기본조건으로_전체게시글_조회시_게시글_이미지가_없으면_대표_이미지는_null() {
        postTestSupport.builder().build();

        final var 게시글_전체_조회_응답 = 게시글을_전체_조회한다();

        final var jsonPath = 게시글_전체_조회_응답.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("content")).hasSize(1);
            softly.assertThat(jsonPath.getLong("content[0].id")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].image")).isNull();
        });
    }

    @Test
    void 기본조건으로_전체게시글_조회시_게시글_이미지가_있으면_대표_이미지는_첫번째_이미지() {
        final Post 게시글 = postTestSupport.builder().build();
        final PostImageInfo 첫번째이미지 = postImageInfoTestSupport.builder().post(게시글).build();
        final PostImageInfo 두번째이미지 = postImageInfoTestSupport.builder().post(게시글).build();

        final var 게시글_전체_조회_응답 = 게시글을_전체_조회한다();

        final var jsonPath = 게시글_전체_조회_응답.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("content")).hasSize(1);
            softly.assertThat(jsonPath.getLong("content[0].id")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].image")).isEqualTo(domain + 첫번째이미지.getStoreName());
        });
    }

    @Test
    void 기본조건으로_전체게시글_조회() {
        한_사용자가_게시글을_서른개_작성한다();

        final var 게시글_전체_조회_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_응답.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("content")).hasSize(20);
            softly.assertThat(jsonPath.getLong("content[0].id")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].title")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].image")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].content")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].writer.nickname")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].createdAt")).isNotNull();
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.viewCount")).isNotNull();
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.commentCount")).isNotNull();
        });
    }

    @Test
    void 한_번에_3개씩_조회하는_조건으로_전체게시글_조회() {
        한_사용자가_게시글을_서른개_작성한다();

        final var 게시글_전체_조회_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .param("size", 3)
                .when()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_응답.body().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("content")).hasSize(3);
            softly.assertThat(jsonPath.getLong("content[0].id")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].title")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].image")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].content")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].writer.nickname")).isNotNull();
            softly.assertThat(jsonPath.getString("content[0].createdAt")).isNotNull();
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.viewCount")).isNotNull();
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.commentCount")).isNotNull();
        });
    }

    @Test
    void 한_번에_3개씩_조회하는_조건으로_2페이지_전체게시글_조회(@Autowired PostReadService postReadService) {
        한_사용자가_게시글을_서른개_작성한다();

        final var 게시글_전체_조회_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .param("size", 3)
                .param("page", 1)
                .when()
                .get("/posts")
                .then()
                .extract();

        final var jsonPath = 게시글_전체_조회_응답.body().jsonPath();

        final var 전체조회_4번째_게시글 = postReadService.findPostsByPagingCondition(
                        GeneralFindingCondition.builder()
                                .size(3)
                                .page(1)
                                .build())
                .getContent().get(0);

        assertSoftly(softly -> {
            softly.assertThat(jsonPath.getList("content")).hasSize(3);
            softly.assertThat(jsonPath.getLong("content[0].id")).isEqualTo(전체조회_4번째_게시글.id());
            softly.assertThat(jsonPath.getString("content[0].title")).isEqualTo(전체조회_4번째_게시글.title());
            softly.assertThat(jsonPath.getString("content[0].image")).isEqualTo(전체조회_4번째_게시글.image());
            softly.assertThat(jsonPath.getString("content[0].content")).isEqualTo(전체조회_4번째_게시글.content());
            softly.assertThat(jsonPath.getString("content[0].writer.nickname"))
                    .isEqualTo(전체조회_4번째_게시글.writer().nickname());
            softly.assertThat(jsonPath.getString("content[0].createdAt")).isNotNull();
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.viewCount"))
                    .isEqualTo(전체조회_4번째_게시글.reactionCount().viewCount());
            softly.assertThat(jsonPath.getInt("content[0].reactionCount.commentCount"))
                    .isEqualTo(전체조회_4번째_게시글.reactionCount().commentCount());
        });
    }

    private void 한_사용자가_게시글을_서른개_작성한다() {
        final Member 작성자 = 사용자를_하나_만든다();
        for (int i = 0; i < 30; i++) {
            게시글을_하나_만든다(작성자);
        }
    }

    @Test
    void 로그인하지_않은_사용자가_게시글_하나를_상세조회하는_경우(@Autowired PostReadService postReadService) {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(게시글_id);

        final SpecificPostInfoResponse 게시글 = postReadService.findSpecificPost(게시글_id,
                new ActiveMemberId(작성자.getId()));

        assertThat(게시글_상세_조회_응답.statusCode()).isEqualTo(200);
        assertAll(
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getLong("id")).isEqualTo(게시글.id()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getString("title")).isEqualTo(게시글.title()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getLong("price")).isEqualTo(게시글.price()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getString("content")).isEqualTo(게시글.content()),

                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getList("images")).hasSize(2),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getString("images[0]")).isEqualTo(게시글.images().get(0)),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getString("images[1]")).isEqualTo(게시글.images().get(1)),

                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getLong("writer.id")).isEqualTo(
                        게시글.writer().id()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getString("writer.nickname")).isEqualTo(
                        게시글.writer().nickname()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getString("writer.profileImage")).isEqualTo(
                        게시글.writer().profileImage()),

                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("reactionCount.viewCount")).isEqualTo(
                        게시글.reactionCount().viewCount()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("reactionCount.commentCount")).isEqualTo(
                        게시글.reactionCount().commentCount()),

                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("upCount")).isEqualTo(게시글.upCount()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("downCount")).isEqualTo(게시글.downCount()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getBoolean("isUp")).isEqualTo(게시글.isUp()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getBoolean("isDown")).isEqualTo(게시글.isDown()),
                () -> assertThat(게시글_상세_조회_응답.body().jsonPath().getBoolean("isWriter")).isFalse()
        );
    }

    @Test
    void 로그인한_사용자가_자신의_게시글_하나를_상세조회하는_경우(@Autowired PostReadService postReadService) {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(작성자, 게시글_id);

        final SpecificPostInfoResponse 게시글 = postReadService.findSpecificPost(게시글_id,
                new ActiveMemberId(작성자.getId()));

        assertThat(게시글_상세_조회_응답.statusCode()).isEqualTo(200);
        assertSoftly(softAssertions -> {
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getLong("id")).isEqualTo(게시글.id());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getString("title")).isEqualTo(게시글.title());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getLong("price")).isEqualTo(게시글.price());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getString("content")).isEqualTo(게시글.content());

                    assertThat(게시글_상세_조회_응답.body().jsonPath().getList("images")).hasSize(2);
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getString("images[0]")).isEqualTo(게시글.images().get(0));
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getString("images[1]")).isEqualTo(게시글.images().get(1));

                    assertThat(게시글_상세_조회_응답.body().jsonPath().getLong("writer.id")).isEqualTo(
                            게시글.writer().id());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getString("writer.nickname")).isEqualTo(
                            게시글.writer().nickname());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getString("writer.profileImage")).isEqualTo(
                            게시글.writer().profileImage());

                    assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("reactionCount.viewCount")).isEqualTo(
                            게시글.reactionCount().viewCount());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("reactionCount.commentCount")).isEqualTo(
                            게시글.reactionCount().commentCount());

                    assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("upCount")).isEqualTo(게시글.upCount());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getInt("downCount")).isEqualTo(게시글.downCount());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getBoolean("isUp")).isEqualTo(게시글.isUp());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getBoolean("isDown")).isEqualTo(게시글.isDown());
                    assertThat(게시글_상세_조회_응답.body().jsonPath().getBoolean("isWriter")).isTrue();
                }
        );
    }

    @Test
    void 로그인한_사용자가_타인의_게시글_하나를_상세조회하는_경우(@Autowired PostReadService postReadService) {
        final Member 작성자 = 사용자를_하나_만든다();
        final Member 작성자가_아닌_사람 = 사용자를_하나_만든다();

        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final ExtractableResponse<Response> 게시글_상세_조회_결과 = 게시글_하나를_상세_조회한다(작성자가_아닌_사람, 게시글_id);

        final SpecificPostInfoResponse 게시글 = postReadService.findSpecificPost(게시글_id,
                new ActiveMemberId(작성자가_아닌_사람.getId()));

        assertThat(게시글_상세_조회_결과.statusCode()).isEqualTo(200);
        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getLong("id")).isEqualTo(게시글.id());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getString("title"))
                            .isEqualTo(게시글.title());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getLong("price")).isEqualTo(게시글.price());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getString("content"))
                            .isEqualTo(게시글.content());

                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getList("images")).hasSize(2);
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getString("images[0]"))
                            .isEqualTo(게시글.images().get(0));
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getString("images[1]"))
                            .isEqualTo(게시글.images().get(1));

                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getLong("writer.id")).isEqualTo(
                            게시글.writer().id());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getString("writer.nickname")).isEqualTo(
                            게시글.writer().nickname());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getString("writer.profileImage"))
                            .isEqualTo(
                                    게시글.writer().profileImage());

                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getInt("reactionCount.viewCount"))
                            .isEqualTo(
                                    게시글.reactionCount().viewCount() - 1);
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getInt("reactionCount.commentCount"))
                            .isEqualTo(
                                    게시글.reactionCount().commentCount());

                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getInt("upCount"))
                            .isEqualTo(게시글.upCount());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getInt("downCount"))
                            .isEqualTo(게시글.downCount());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getBoolean("isUp")).isEqualTo(게시글.isUp());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getBoolean("isDown"))
                            .isEqualTo(게시글.isDown());
                    softAssertions.assertThat(게시글_상세_조회_결과.body().jsonPath().getBoolean("isWriter")).isEqualTo(false);
                }
        );
    }

    @Test
    void 자신이_작성한_게시글은_수정_가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);
        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(작성자, 게시글_id);

        final String 유지할_이미지의_url = 게시글_상세_조회_응답.body().jsonPath().getString("images[0]");

        final String sessionId = 로그인(작성자);

        final ExtractableResponse<Response> 게시글_수정_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "제목을 수정하자")
                .multiPart("content", "내용을 수정하자")
                .multiPart("price", 10000)
                .multiPart("originalImages", 유지할_이미지의_url)
                .multiPart("newImages", 이미지1)
                .when()
                .put("/posts/" + 게시글_id)
                .then()
                .extract();

        assertThat(게시글_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 타인이_작성한_게시글은_수정_불가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final Member 작성자가_아닌_사람 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(작성자, 게시글_id);

        final String 유지할_이미지의_url = 게시글_상세_조회_응답.body().jsonPath().getString("images[0]");

        final String sessionId = 로그인(작성자가_아닌_사람);

        final ExtractableResponse<Response> 게시글_수정_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "제목을 수정하자")
                .multiPart("content", "내용을 수정하자")
                .multiPart("price", 10000)
                .multiPart("originalImages", 유지할_이미지의_url)
                .multiPart("newImages", 이미지1)
                .when()
                .put("/posts/" + 게시글_id)
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(게시글_수정_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                    softly.assertThat(게시글_수정_응답.jsonPath().getInt("errorCode")).isEqualTo(POST_MEMBER_NOT_SAME.getCode());
                    softly.assertThat(게시글_수정_응답.jsonPath().getString("errorMessage"))
                            .isEqualTo(POST_MEMBER_NOT_SAME.getMessage());
                }
        );
    }

    @Test
    void 로그인하지_않으면_게시글은_수정_불가능하다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(게시글_id);

        final String 유지할_이미지의_url = 게시글_상세_조회_응답.body().jsonPath().getString("images[0]");

        final ExtractableResponse<Response> 게시글_수정_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .build()
                .multiPart("title", "제목을 수정하자")
                .multiPart("content", "내용을 수정하자")
                .multiPart("price", 10000)
                .multiPart("originalImages", 유지할_이미지의_url)
                .multiPart("newImages", 이미지1)
                .when()
                .put("/posts/" + 게시글_id)
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(게시글_수정_응답.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                    softly.assertThat(게시글_수정_응답.jsonPath().getInt("errorCode")).isEqualTo(AUTHORIZATION_EMPTY.getCode());
                    softly.assertThat(게시글_수정_응답.jsonPath().getString("errorMessage"))
                            .isEqualTo(AUTHORIZATION_EMPTY.getMessage());
                }
        );
    }

    @Test
    void 게시글을_수정할때_이미지_url의_도메인_주소가_잘못되었다면_예외가_발생한다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final String sessionId = 로그인(작성자);

        final ExtractableResponse<Response> 게시글_수정_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "제목을 수정하자")
                .multiPart("content", "내용을 수정하자")
                .multiPart("price", 10000)
                .multiPart("originalImages", "이상한url이지롱")
                .multiPart("newImages", 이미지1)
                .when()
                .put("/posts/" + 게시글_id)
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(게시글_수정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    softly.assertThat(게시글_수정_응답.jsonPath().getInt("errorCode")).isEqualTo(IMAGE_DOMAIN_INVALID.getCode());
                    softly.assertThat(게시글_수정_응답.jsonPath().getString("errorMessage"))
                            .isEqualTo(IMAGE_DOMAIN_INVALID.getMessage());
                }
        );
    }

    @Test
    void 게시글을_수정할때_이미지_이름이_기존_게시글에_있는_이미지_이름이_아니면_예외가_발생한다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);

        final String sessionId = 로그인(작성자);

        final ExtractableResponse<Response> 게시글_수정_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "제목을 수정하자")
                .multiPart("content", "내용을 수정하자")
                .multiPart("price", 10000)
                .multiPart("originalImages", domain + "없는이미지.jpg")
                .multiPart("newImages", 이미지1)
                .sessionId(sessionId)
                .when()
                .put("/posts/" + 게시글_id)
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(게시글_수정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    softly.assertThat(게시글_수정_응답.jsonPath().getInt("errorCode")).isEqualTo(IMAGE_STORE_NAME_INVALID.getCode());
                    softly.assertThat(게시글_수정_응답.jsonPath().getString("errorMessage"))
                            .isEqualTo(IMAGE_STORE_NAME_INVALID.getMessage());
                }
        );
    }

    @Test
    void 게시글을_수정할때_이미지가_10개_이상이면_예외가_발생한다() {
        final Member 작성자 = 사용자를_하나_만든다();
        final ExtractableResponse<Response> 게시글_생성_응답 = 게시글을_하나_만든다(작성자);
        final long 게시글_id = 응답의_location헤더에서_id를_추출한다(게시글_생성_응답);
        final ExtractableResponse<Response> 게시글_상세_조회_응답 = 게시글_하나를_상세_조회한다(작성자, 게시글_id);

        final String 유지할_이미지의_url = 게시글_상세_조회_응답.body().jsonPath().getString("images[0]");

        final String sessionId = 로그인(작성자);

        final ExtractableResponse<Response> 게시글_수정_응답 = EdonymyeonRestAssured.builder()
                .version(1)
                .sessionId(sessionId)
                .build()
                .multiPart("title", "제목을 수정하자")
                .multiPart("content", "내용을 수정하자")
                .multiPart("price", 10000)
                .multiPart("originalImages", 유지할_이미지의_url)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .multiPart("newImages", 이미지1)
                .sessionId(sessionId)
                .when()
                .put("/posts/" + 게시글_id)
                .then()
                .extract();

        assertSoftly(softly -> {
                    softly.assertThat(게시글_수정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    softly.assertThat(게시글_수정_응답.jsonPath().getInt("errorCode")).isEqualTo(POST_IMAGE_COUNT_INVALID.getCode());
                    softly.assertThat(게시글_수정_응답.jsonPath().getString("errorMessage"))
                            .isEqualTo(POST_IMAGE_COUNT_INVALID.getMessage());
                }
        );
    }
}
