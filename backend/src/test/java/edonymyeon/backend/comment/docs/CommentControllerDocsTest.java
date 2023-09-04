package edonymyeon.backend.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.comment.application.CommentService;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.TestMemberBuilder;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((사용자.getEmail() + ":" + 사용자.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("comment-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                ),
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
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((사용자.getEmail() + ":" + 사용자.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("comment-delete",
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id"),
                        parameterWithName("commentId").description("댓글 id")
                )
        );

        mockMvc.perform(댓글_삭제_요청)
                .andExpect(status().isNoContent())
                .andDo(문서화);
    }
}
