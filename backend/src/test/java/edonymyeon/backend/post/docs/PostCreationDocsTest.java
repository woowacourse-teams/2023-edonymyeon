package edonymyeon.backend.post.docs;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
public class PostCreationDocsTest implements ImageFileCleaner {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmail(회원.getEmail())).thenReturn(
                Optional.of(회원));
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));
    }

    @Test
    void 게시글을_생성한다() throws Exception {
        final Member 글쓴이 = new Member(1L, "email", "password", "nickname", null);
        회원_레포지토리를_모킹한다(글쓴이);

        Part 제목 = new MockPart("title", null, "제목입니다".getBytes(StandardCharsets.UTF_8));
        Part 내용 = new MockPart("content", null, "내용입니다".getBytes(StandardCharsets.UTF_8));
        Part 가격 = new MockPart("price", null, "100000".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile 이미지 = new MockMultipartFile("newImages", "image.jpg", ContentType.IMAGE_JPEG.getMimeType(),
                "이미지 파일입니다".getBytes(StandardCharsets.UTF_8));

        final MockHttpServletRequestBuilder 게시글_작성_요청 = multipart("/posts")
                .part(제목)
                .part(내용)
                .part(가격)
                .file(이미지)
                .file(이미지)
                .file(이미지)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((글쓴이.getEmail() + ":" + 글쓴이.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("post-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                ),
                requestParts(
                        partWithName("title").description("제목"),
                        partWithName("content").description("내용"),
                        partWithName("price").description("가격"),
                        partWithName("newImages").description("이미지 파일들")
                ),
                responseFields(
                        fieldWithPath("id").type(Long.TYPE).description("게시글 id")
                )
        );

        this.mockMvc.perform(게시글_작성_요청)
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/posts/" + 1))
                .andDo(문서화);
    }
}