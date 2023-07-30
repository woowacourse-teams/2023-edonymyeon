package edonymyeon.backend.post.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.application.ThumbsService;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
import org.springframework.http.HttpMethod;
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
public class PostControllerDocsTest implements ImageFileCleaner {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostImageInfoRepository postImageInfoRepository;

    @MockBean
    private ThumbsService thumbsService;

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmailAndPassword(회원.getEmail(), 회원.getPassword())).thenReturn(
                Optional.of(회원));
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));
    }

    private void 게시글_레포지토리를_모킹한다(final Post 게시글) {
        when(postRepository.findById(게시글.getId())).thenReturn(Optional.of(게시글));
        doNothing().when(postRepository).deleteById(게시글.getId());
    }

    private void 게시글_이미지_정보_레포지토리를_모킹한다() {
        doNothing().when(postImageInfoRepository).deleteAll(any());
    }

    private void 추천_레포지토리를_모킹한다(final Post 게시글) {
        doNothing().when(thumbsService).deleteAllThumbsInPost(게시글.getId());
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
                .andDo(문서화);
    }

    @Test
    void 게시글을_수정한다() throws Exception {
        final Member 글쓴이 = new Member(1L, "email", "password", "nickname", null);
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이, PostImageInfos.create(), LocalDateTime.now(), 0L);
        final PostImageInfo 유지하는_이미지_정보 = new PostImageInfo("stay.jpg", 게시글);
        final PostImageInfo 삭제될_이미지_정보 = new PostImageInfo("delete.jpg", 게시글);
        게시글.addPostImageInfo(유지하는_이미지_정보);
        게시글.addPostImageInfo(삭제될_이미지_정보);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        게시글_이미지_정보_레포지토리를_모킹한다();

        Part 제목 = new MockPart("title", null, "제목입니다".getBytes(StandardCharsets.UTF_8));
        Part 내용 = new MockPart("content", null, "내용입니다".getBytes(StandardCharsets.UTF_8));
        Part 가격 = new MockPart("price", null, "100000".getBytes(StandardCharsets.UTF_8));
        Part 유지하는_이미지 = new MockPart("originalImages", null,
                "http://edonymyeon.site/images/stay.jpg".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile 추가되는_이미지 = new MockMultipartFile("newImages", "image.jpg",
                ContentType.IMAGE_JPEG.getMimeType(),
                "이미지 파일입니다".getBytes(StandardCharsets.UTF_8));

        final MockHttpServletRequestBuilder 게시글_수정_요청 = multipart("/posts/{postId}", 게시글.getId())
                .part(제목)
                .part(내용)
                .part(가격)
                .part(유지하는_이미지)
                .file(추가되는_이미지)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((글쓴이.getEmail() + ":" + 글쓴이.getPassword()).getBytes()));
        게시글_수정_요청.with(request -> {
            request.setMethod(HttpMethod.PUT.name());
            return request;
        });

        final RestDocumentationResultHandler 문서화 = document("post-update",
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ),
                requestParts(
                        partWithName("title").description("제목"),
                        partWithName("content").description("내용"),
                        partWithName("price").description("가격"),
                        partWithName("originalImages").description("기존 이미지 중에 유지하는 이미지의 url"),
                        partWithName("newImages").description("새로 추가되는 이미지 파일들")
                ),
                responseFields(
                        fieldWithPath("id").type(Long.TYPE).description("게시글 id")
                )
        );

        this.mockMvc.perform(게시글_수정_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 게시글을_삭제한다() throws Exception {
        final Member 글쓴이 = new Member(1L, "email", "password", "nickname", null);
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이, PostImageInfos.create(), LocalDateTime.now(), 0L);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        게시글_이미지_정보_레포지토리를_모킹한다();
        추천_레포지토리를_모킹한다(게시글);

        final MockHttpServletRequestBuilder 게시글_삭제_요청 = delete("/posts/{postId}", 게시글.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((글쓴이.getEmail() + ":" + 글쓴이.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("post-delete",
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                )
        );

        this.mockMvc.perform(게시글_삭제_요청)
                .andExpect(status().isNoContent())
                .andDo(문서화);
    }
}
