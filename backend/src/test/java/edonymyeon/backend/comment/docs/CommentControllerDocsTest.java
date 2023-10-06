package edonymyeon.backend.comment.docs;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.comment.application.CommentService;
import edonymyeon.backend.comment.application.dto.response.CommentDto;
import edonymyeon.backend.comment.application.dto.response.CommentsResponse;
import edonymyeon.backend.comment.application.dto.response.WriterDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.TestMemberBuilder;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@IntegrationTest
public class CommentControllerDocsTest {

    private final MockMvc mockMvc;

    @MockBean
    private final CommentService commentService;

    private final TestMemberBuilder memberTestSupport;

    @Test
    void 댓글을_작성한다() throws Exception {
        final Member 사용자 = memberTestSupport.builder().build();
        when(commentService.createComment(any(), anyLong(), any())).thenReturn(1L);

        Part 내용 = new MockPart("comment", null, "댓글내용입니다".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile 이미지 = new MockMultipartFile("image", "image.jpg", ContentType.IMAGE_JPEG.getMimeType(),
                "이미지 파일입니다".getBytes(StandardCharsets.UTF_8));

        final MockHttpServletRequestBuilder 댓글_작성_요청 = multipart("/posts/{postId}/comments", 1L)
                .part(내용)
                .file(이미지)
                .header("X-API-VERSION", 1)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .sessionAttr(USER.getSessionId(), 사용자.getId());

        final RestDocumentationResultHandler 문서화 = document("comment-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ),
                requestParts(
                        partWithName("image").description("이미지 파일"),
                        partWithName("comment").description("댓글 내용")
                )
        );

        mockMvc.perform(댓글_작성_요청)
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/posts/1/comments/" + 1))
                .andDo(문서화);
    }

    @Test
    void 댓글을_삭제한다() throws Exception {
        final Member 사용자 = memberTestSupport.builder().build();
        doNothing().when(commentService).deleteComment(any(), anyLong(), anyLong());

        final MockHttpServletRequestBuilder 댓글_삭제_요청 = delete("/posts/{postId}/comments/{commentId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-VERSION", 1)
                .sessionAttr(USER.getSessionId(), 사용자.getId());

        final RestDocumentationResultHandler 문서화 = document("comment-delete",
                pathParameters(
                        parameterWithName("postId").description("게시글 id"),
                        parameterWithName("commentId").description("댓글 id")
                )
        );

        mockMvc.perform(댓글_삭제_요청)
                .andExpect(status().isNoContent())
                .andDo(문서화);
    }

    @Test
    void 댓글을_조회한다() throws Exception {
        final CommentsResponse commentsResponse = new CommentsResponse(
                3,
                List.of(
                        new CommentDto(
                                4L,
                                null,
                                "그건 너무 비싸용",
                                false,
                                LocalDateTime.now(),
                                new WriterDto("훈수쟁이")
                        ),
                        new CommentDto(
                                8L,
                                "http://edonymyeon.site/images/comment/image2.png",
                                "그돈이면 치킨이 세마리",
                                false,
                                LocalDateTime.now(),
                                new WriterDto("훈수충")
                        ),
                        new CommentDto(
                                9L,
                                "http://edonymyeon.site/images/comment/image3.png",
                                "배고프다",
                                false,
                                LocalDateTime.now(),
                                new WriterDto("거지")
                        )
                )
        );
        when(commentService.findCommentsByPostId(any(), anyLong())).thenReturn(commentsResponse);

        final MockHttpServletRequestBuilder 댓글_조회_요청 = get("/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-VERSION", 1);

        final RestDocumentationResultHandler 문서화 = document("comment-findAllByPost",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ),
                responseFields(
                        fieldWithPath("commentCount").description("댓글 수"),
                        fieldWithPath("comments[].id").description("댓글 id"),
                        fieldWithPath("comments[].image").description("댓글에 첨부된 사진의 url").optional(),
                        fieldWithPath("comments[].comment").description("댓글 본문"),
                        fieldWithPath("comments[].isWriter").description("댓글 조회 요청을 보낸 사용자가 댓글 작성자 본인인지 여부"),
                        fieldWithPath("comments[].createdAt").description("댓글 작성 시각"),
                        fieldWithPath("comments[].writer.nickname").description("댓글 작성자의 닉네임")
                )
        );

        mockMvc.perform(댓글_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }
}
