package edonymyeon.backend.member.docs;

import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.support.DocsTest;
import edonymyeon.backend.support.TestMemberBuilder;
import jakarta.servlet.http.Part;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.apache.http.entity.ContentType;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@SuppressWarnings("NonAsciiCharacters")
class MemberControllerDocsTest extends DocsTest {

    private final TestMemberBuilder testMemberBuilder = new TestMemberBuilder(null);

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private ConsumptionRepository consumptionRepository;

    public MemberControllerDocsTest(final MockMvc mockMvc,
                                    final ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmail(회원.getEmail())).thenReturn(Optional.of(회원));
    }

    private void 게시글_레포지토리를_모킹한다(final Post 게시글) {
        when(postRepository.findById(게시글.getId())).thenReturn(Optional.of(게시글));
    }

    private void 소비_레포지토리를_모킹한다(final Consumption 소비) {
        when(consumptionRepository.save(소비)).thenReturn(소비);
    }

    private void 소비_레포지토리를_모킹한다(final Post 게시글, final Consumption 소비) {
        when(consumptionRepository.findByPostId(게시글.getId())).thenReturn(Optional.of(소비));
        doNothing().when(consumptionRepository).delete(any());
    }

    @Test
    void 구매_확정한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 회원);
        final Consumption 소비 = Consumption.of(게시글, SAVING, null, 2023, 7);

        회원_레포지토리를_모킹한다(회원);
        게시글_레포지토리를_모킹한다(게시글);
        소비_레포지토리를_모킹한다(소비);

        final PurchaseConfirmRequest request = new PurchaseConfirmRequest(800L, 2023, 7);

        final MockHttpServletRequestBuilder 구매_확정_요청 = post("/profile/my-posts/{postId}/purchase-confirm", 게시글.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-VERSION", 1)
                .sessionAttr(USER.getSessionId(), 회원.getId())
                .content(objectMapper.writeValueAsString(request));

        final RestDocumentationResultHandler 문서화 = document("purchase-confirm",
                preprocessRequest(prettyPrint()),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ),
                requestFields(
                        fieldWithPath("purchasePrice").description("구매 확정 금액"),
                        fieldWithPath("year").description("구매 확정 년도"),
                        fieldWithPath("month").description("구매 확정 달")
                )
        );

        this.mockMvc.perform(구매_확정_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 절약_확정한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder().id(1L).buildWithoutSaving();
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 회원);
        final Consumption 소비 = Consumption.of(게시글, SAVING, null, 2023, 7);

        회원_레포지토리를_모킹한다(회원);
        게시글_레포지토리를_모킹한다(게시글);
        소비_레포지토리를_모킹한다(소비);

        final SavingConfirmRequest request = new SavingConfirmRequest(2023, 7);

        final MockHttpServletRequestBuilder 절약_확정_요청 = post("/profile/my-posts/{postId}/saving-confirm", 게시글.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-VERSION", 1)
                .sessionAttr(USER.getSessionId(), 회원.getId())
                .content(objectMapper.writeValueAsString(request));

        final RestDocumentationResultHandler 문서화 = document("saving-confirm",
                preprocessRequest(prettyPrint()),
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                ),
                requestFields(
                        fieldWithPath("year").description("절약 확정 년도"),
                        fieldWithPath("month").description("절약 확정 달")
                )
        );

        this.mockMvc.perform(절약_확정_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 확정을_취소한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder().id(1L).buildWithoutSaving();
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 회원);
        final Consumption 소비 = Consumption.of(게시글, SAVING, null, 2023, 7);

        회원_레포지토리를_모킹한다(회원);
        게시글_레포지토리를_모킹한다(게시글);
        소비_레포지토리를_모킹한다(게시글, 소비);

        final MockHttpServletRequestBuilder 확정_취소_요청 = delete("/profile/my-posts/{postId}/confirm-remove", 게시글.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-VERSION", 1)
                .sessionAttr(USER.getSessionId(), 회원.getId());

        final RestDocumentationResultHandler 문서화 = document("confirm-remove",
                pathParameters(
                        parameterWithName("postId").description("게시글 id")
                )
        );

        this.mockMvc.perform(확정_취소_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 회원_탈퇴한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder().id(1L).buildWithoutSaving();
        회원_레포지토리를_모킹한다(회원);
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));

        final MockHttpServletRequestBuilder 회원_탈퇴_요청 = delete("/withdraw")
                .header("X-API-VERSION", 1)
                .sessionAttr(USER.getSessionId(), 회원.getId());

        final RestDocumentationResultHandler 문서화 = document("withdraw");

        this.mockMvc.perform(회원_탈퇴_요청)
                .andExpect(status().isNoContent())
                .andDo(문서화);
    }

    @Test
    void 회원정보를_수정한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder().id(1L).buildWithoutSaving();
        회원_레포지토리를_모킹한다(회원);
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "image.jpg",
                ContentType.IMAGE_JPEG.getMimeType(),
                "이미지 파일입니다".getBytes(StandardCharsets.UTF_8));
        Part nickname = new MockPart("nickname", null, "수정할 닉네임".getBytes(StandardCharsets.UTF_8));

        final MockHttpServletRequestBuilder 회원_정보_수정_요청 = MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/profile")
                .part(nickname)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + Base64.getEncoder()
                        .encodeToString((회원.getEmail() + ":" + 회원.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("profile-update",
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")),
                requestParts(
                        partWithName("nickname").description("수정할 닉네임"),
                        partWithName("profileImage").description("수정할 프로필 사진")
                )
        );

        this.mockMvc.perform(회원_정보_수정_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }
}
