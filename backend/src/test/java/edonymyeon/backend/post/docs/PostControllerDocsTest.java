package edonymyeon.backend.post.docs;

import edonymyeon.backend.CacheConfig;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Device;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.application.dto.AllThumbsInPostResponse;
import edonymyeon.backend.post.application.dto.ThumbsStatusInPostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.application.PostThumbsServiceImpl;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@IntegrationTest
@Import(CacheConfig.class)
public class PostControllerDocsTest implements ImageFileCleaner {

    private final MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private PostImageInfoRepository postImageInfoRepository;

    @MockBean
    private PostThumbsServiceImpl postThumbsService;

    private static Member makeMember() {
        return new Member(1L, "email", "password", "nickname", null, null);
    }

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmail(회원.getEmail())).thenReturn(
                Optional.of(회원));
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));
    }

    private void 게시글_레포지토리를_모킹한다(final Post 게시글) {
        when(postRepository.findById(게시글.getId())).thenReturn(Optional.of(게시글));
        doNothing().when(postRepository).deleteById(게시글.getId());
        when(postRepository.findAllBy(any())).thenReturn(new SliceImpl<>(List.of(게시글)));
    }

    private void 게시글_검색을_모킹한다(final Post 게시글) {
        when(postRepository.findAll((Specification<Post>) any(), (Pageable) any())).thenReturn(
                new PageImpl<>(List.of(게시글)));
    }

    private void 핫_게시글_조회를_모킹한다(final Post 게시글) {
        when(postRepository.findHotPosts(any(LocalDateTime.class), anyInt(), anyInt(), any()))
                .thenReturn(new SliceImpl<>(List.of(게시글)));
        when(postRepository.findByIds(anyList()))
                .thenReturn(List.of(게시글));
    }

    private void 게시글_이미지_정보_레포지토리를_모킹한다() {
        doNothing().when(postImageInfoRepository).deleteAll(any());
    }

    private void 추천_서비스를_모킹한다(final Member 회원, final Post 게시글) {
        doNothing().when(postThumbsService).deleteAllThumbsInPost(게시글.getId());
        when(postThumbsService
                .findAllThumbsInPost(게시글.getId()))
                .thenReturn(new AllThumbsInPostResponse(4, 5));
        when(postThumbsService
                .findThumbsStatusInPost(new ActiveMemberId(회원.getId()), 게시글.getId()))
                .thenReturn(new ThumbsStatusInPostResponse(false, false));
    }

    @Test
    void 게시글을_수정한다() throws Exception {
        final Member 글쓴이 = new Member(1L, "email", "password", "nickname", null, new Device("kj234jkn342kj"));
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이);
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
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                ),
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
        final Member 글쓴이 = new Member(1L, "email", "password", "nickname", null, new Device("kj234jkn342kj"));
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        게시글_이미지_정보_레포지토리를_모킹한다();
        추천_서비스를_모킹한다(글쓴이, 게시글);

        final MockHttpServletRequestBuilder 게시글_삭제_요청 = delete("/posts/{postId}", 게시글.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((글쓴이.getEmail() + ":" + 글쓴이.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("post-delete",
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                ),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                )
        );

        this.mockMvc.perform(게시글_삭제_요청)
                .andExpect(status().isNoContent())
                .andDo(문서화);
    }

    @Test
    void 게시글을_전체_조회한다() throws Exception {
        final Member 글쓴이 = makeMember();
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이, PostImageInfos.create(), 0);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);

        final var 게시글_전체_조회_요청 = get("/posts")
                .param("size", "10")
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        final RestDocumentationResultHandler 문서화 = document("post-findAll",
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("size").description("한 페이지 당 조회할 게시글 수"),
                        parameterWithName("page").description("조회할 게시글의 페이지")
                )
        );

        this.mockMvc.perform(게시글_전체_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 게시글을_검색한다() throws Exception {
        final GeneralFindingCondition findingCondition = GeneralFindingCondition.builder().build();

        final Member 글쓴이 = makeMember();
        final Post 게시글 = new Post(1L, "햄버거 먹어도 되나요", "불고기 버거 세일중이던데", 1000L, 글쓴이, PostImageInfos.create(), 0);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        게시글_검색을_모킹한다(게시글);

        final var 게시글_검색_요청 = get("/search")
                .queryParam("query", "세일 햄버거")
                .queryParam("page", findingCondition.getPage().toString())
                .queryParam("size", findingCondition.getSize().toString())
                .queryParam("sort-by", findingCondition.getSortBy().getName())
                .queryParam("sort-direction", findingCondition.getSortDirection().name())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        FieldDescriptor[] 응답 = {
                fieldWithPath("content").description("게시글 목록"),
                fieldWithPath("content[].id").description("게시글 id"),
                fieldWithPath("content[].title").description("게시글 제목"),
                fieldWithPath("content[].image").description("게시글 이미지 url"),
                fieldWithPath("content[].content").description("게시글 내용"),
                fieldWithPath("content[].createdAt").description("게시글 생성 날짜"),
                fieldWithPath("content[].writer.nickName").description("닉네임"),
                fieldWithPath("content[].reactionCount.viewCount").description("조회수"),
                fieldWithPath("content[].reactionCount.commentCount").description("댓글 수"),
                fieldWithPath("content[].reactionCount.scrapCount").description("스크랩수"),
                fieldWithPath("last").description("현재 요청한 페이지가 마지막 페이지인지")
        };

        final RestDocumentationResultHandler 문서화 = document("post-search",
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("size").description("한 페이지 당 조회할 게시글 수"),
                        parameterWithName("page").description("조회할 게시글의 페이지"),
                        parameterWithName("query").description("검색하는 단어"),
                        parameterWithName("sort-by").description("정렬 기준"),
                        parameterWithName("sort-direction").description("정렬 방향")
                ),
                responseFields(응답)
        );

        this.mockMvc.perform(게시글_검색_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 핫_게시글을_조회한다() throws Exception {
        final HotFindingCondition findingCondition = HotFindingCondition.builder().build();

        final Member 글쓴이 = makeMember();
        final Post 게시글 = new Post(1L, "햄버거 먹어도 되나요", "불고기 버거 세일중이던데", 1000L, 글쓴이, PostImageInfos.create(), 0);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        핫_게시글_조회를_모킹한다(게시글);

        final var 핫_게시글_조회_요청 = get("/posts/hot")
                .queryParam("page", findingCondition.getPage().toString())
                .queryParam("size", findingCondition.getSize().toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        FieldDescriptor[] 응답 = {
                fieldWithPath("content").description("게시글 목록"),
                fieldWithPath("content[].id").description("게시글 id"),
                fieldWithPath("content[].title").description("게시글 제목"),
                fieldWithPath("content[].image").description("게시글 이미지 url"),
                fieldWithPath("content[].content").description("게시글 내용"),
                fieldWithPath("content[].createdAt").description("게시글 생성 날짜"),
                fieldWithPath("content[].writer.nickName").description("닉네임"),
                fieldWithPath("content[].reactionCount.viewCount").description("조회수"),
                fieldWithPath("content[].reactionCount.commentCount").description("댓글 수"),
                fieldWithPath("content[].reactionCount.scrapCount").description("스크랩수"),
                fieldWithPath("last").description("현재 요청한 페이지가 마지막 페이지인지")
        };

        final RestDocumentationResultHandler 문서화 = document("post-hotPosts",
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("size").description("한 페이지 당 조회할 게시글 수"),
                        parameterWithName("page").description("조회할 게시글의 페이지")
                ),
                responseFields(응답)
        );

        this.mockMvc.perform(핫_게시글_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 게시글을_상세_조회한다() throws Exception {
        final Member 글쓴이 = makeMember();
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이, PostImageInfos.create(), 0);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        추천_서비스를_모킹한다(글쓴이, 게시글);

        final var 게시글_상세_조회_요청 = get("/posts/{postId}", 게시글.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((글쓴이.getEmail() + ":" + 글쓴이.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("post-findOne",
                preprocessResponse(prettyPrint())
        );

        this.mockMvc.perform(게시글_상세_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }
}
