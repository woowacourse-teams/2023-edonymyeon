package edonymyeon.backend.post.docs;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import edonymyeon.backend.support.DocsTest;
import edonymyeon.backend.support.TestMemberBuilder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
public class MyPostControllerDocsTest extends DocsTest {

    @Autowired
    private final Domain domain;

    TestMemberBuilder testMemberBuilder = new TestMemberBuilder(null);

    @MockBean
    private MyPostService myPostService;

    @MockBean
    private MemberRepository memberRepository;

    public MyPostControllerDocsTest(final MockMvc mockMvc,
                                    final ObjectMapper objectMapper,
                                    final Domain domain) {
        super(mockMvc, objectMapper);
        this.domain = domain;
    }

    @Test
    void 내_게시글_조회_문서화() throws Exception {
        final GeneralFindingCondition findingCondition = GeneralFindingCondition.builder().build();

        final Member 회원 = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();
        회원_레포지토리를_모킹한다(회원);

        final Post 게시글1 = new Post(1L, "제목1", "내용1", 1000L, 회원, PostImageInfos.create(), 0, 0, false);
        final Post 게시글2 = new Post(2L, "제목2", "내용2", 2000L, 회원, PostImageInfos.create(), 0, 0, false);

        final MyPostResponse postResponse1 = MyPostResponse.of(게시글1, domain, PostConsumptionResponse.none());
        final MyPostResponse postResponse2 = MyPostResponse.of(게시글2, domain, PostConsumptionResponse.none());

        final List<MyPostResponse> posts = List.of(postResponse1, postResponse2);

        final Slice<MyPostResponse> result = new SliceImpl<>(posts, findingCondition.toPage(), false);

        when(myPostService.findMyPosts(any(), any())).thenReturn(PostSlice.from(result));

        final MockHttpServletRequestBuilder 내_게시글_조회_요청 = get("/profile/my-posts")
                .sessionAttr(USER.getSessionId(), 회원.getId())
                .header("X-API-VERSION", 1)
                .queryParam("page", findingCondition.getPage().toString())
                .queryParam("size", findingCondition.getSize().toString())
                .queryParam("sort-by", findingCondition.getSortBy().getName())
                .queryParam("sort-direction", findingCondition.getSortDirection().name());

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
                fieldWithPath("content[].reactionCount.viewCount").description("조회수"),
                fieldWithPath("content[].reactionCount.commentCount").description("댓글 수"),
                fieldWithPath("last").description("현재 요청한 페이지가 마지막 페이지인지")
        };

        final RestDocumentationResultHandler 문서화 = document("my-posts",
                preprocessResponse(prettyPrint()),
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
