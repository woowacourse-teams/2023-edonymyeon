package edonymyeon.backend.post.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.DocsTest;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.MyPostService;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.application.dto.response.PostConsumptionResponse;
import edonymyeon.backend.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
public class MyPostControllerDocsTest extends DocsTest {

    @MockBean
    private MyPostService myPostService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private Domain domain;

    @Test
    void 내_게시글_조회_문서화() throws Exception {
        final GeneralFindingCondition findingCondition = GeneralFindingCondition.builder().build();

        Member 회원 = new Member("example@example.com", "password11234!", "testNickname", null);
        회원_레포지토리를_모킹한다(회원);

        final Post 게시글1 = new Post(1L, "제목1", "내용1", 1000L, 회원, PostImageInfos.create(), LocalDateTime.now(), 0);
        final Post 게시글2 = new Post(2L, "제목2", "내용2", 2000L, 회원, PostImageInfos.create(), LocalDateTime.now(), 0);

        final MyPostResponse postResponse1 = MyPostResponse.of(게시글1, domain, PostConsumptionResponse.none());
        final MyPostResponse postResponse2 = MyPostResponse.of(게시글2, domain, PostConsumptionResponse.none());

        final List<MyPostResponse> posts = List.of(postResponse1, postResponse2);

        final Slice<MyPostResponse> result = new SliceImpl<>(posts, findingCondition.toPage(), false);

        when(myPostService.findMyPosts(any(), any())).thenReturn(PostSlice.from(result));

        final MockHttpServletRequestBuilder 내_게시글_조회_요청 = get("/profile/my-posts")
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((회원.getEmail() + ":" + 회원.getPassword()).getBytes()))
                .queryParam("page", findingCondition.getPage().toString())
                .queryParam("size", findingCondition.getSize().toString())
                .queryParam("sort-by", findingCondition.getSortBy().getName())
                .queryParam("sort-direction", findingCondition.getSortDirection().name());

        HeaderDescriptor[] 요청_헤더 = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
        };

        ParameterDescriptor[] 요청_쿼리_파라미터 = {
                parameterWithName("page")
                        .optional()
                        .description(String.format("요청할 페이지 인덱스 (기본: %d)", findingCondition.getPage())),
                parameterWithName("size")
                        .optional()
                        .description(String.format("요청할 게시글 개수 (기본: %d)", findingCondition.getSize())),
                parameterWithName("sort-by")
                        .optional()
                        .description(String.format("정렬 기준 (기본: %s)", findingCondition.getSortBy().getName())),
                parameterWithName("sort-direction")
                        .optional()
                        .description(String.format("정렬 방향 (기본: %s)", findingCondition.getSortDirection().name()))
        };
        FieldDescriptor[] 응답 = {
                fieldWithPath("content").description("게시글 목록"),
                fieldWithPath("content[].id").description("게시글 id"),
                fieldWithPath("content[].title").description("게시글 제목"),
                fieldWithPath("content[].image").description("게시글 이미지 url"),
                fieldWithPath("content[].content").description("게시글 내용"),
                fieldWithPath("content[].createdAt").description("게시글 생성 날짜"),
                fieldWithPath("content[].consumption").description("게시글 소비 정보"),
                fieldWithPath("content[].consumption.type").description("소비 확정 상태"),
                fieldWithPath("content[].consumption.purchasePrice").description("소비 확정시 구매 가격 (확정 x의 경우: 0)"),
                fieldWithPath("content[].consumption.year").description("소비 확정 연도 (확정 x의 경우: 0)"),
                fieldWithPath("content[].consumption.month").description("소비 확정 달 (확정 x의 경우: 0)"),
                fieldWithPath("last").description("현재 요청한 페이지가 마지막 페이지인지")
        };

        final RestDocumentationResultHandler 문서화 = document("my-posts",
                preprocessResponse(prettyPrint()),
                requestHeaders(요청_헤더),
                queryParameters(요청_쿼리_파라미터),
                responseFields(응답)
        );

        mockMvc.perform(내_게시글_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmail(회원.getEmail())).thenReturn(Optional.of(회원));
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));
    }
}
