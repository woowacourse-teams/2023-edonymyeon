package edonymyeon.backend.report.docs;

import static edonymyeon.backend.member.auth.ui.SessionConst.USER;
import static edonymyeon.backend.report.domain.ReportType.POST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.content.post.postimage.domain.PostImageInfos;
import edonymyeon.backend.member.profile.domain.Email;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.member.profile.repository.MemberRepository;
import edonymyeon.backend.content.post.ImageFileCleaner;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.content.post.repository.PostRepository;
import edonymyeon.backend.report.application.ReportRepository;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.report.domain.AbusingType;
import edonymyeon.backend.report.domain.Report;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.TestMemberBuilder;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@IntegrationTest
class ReportDocsTest implements ImageFileCleaner {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final TestMemberBuilder testMemberBuilder;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private ReportRepository reportRepository;

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmail(Email.from(회원.getEmail()))).thenReturn(
                Optional.of(회원));
        when(memberRepository.findById(회원.getId())).thenReturn(Optional.of(회원));
    }

    private void 게시글_레포지토리를_모킹한다(final Post 게시글) {
        when(postRepository.findById(게시글.getId())).thenReturn(Optional.of(게시글));
        doNothing().when(postRepository).deleteById(게시글.getId());
        when(postRepository.findAllBy(any())).thenReturn(new SliceImpl<>(List.of(게시글)));
    }

    private void 신고_서비스를_모킹한다(final Report 신고) {
        when(reportRepository.save(any()))
                .thenReturn(new Report(POST, 신고.getReferenceId(), 신고.getReporter(), AbusingType.OBSCENITY, ""));
    }

    @Test
    void 게시글을_상세_조회한다() throws Exception {
        final Member 글쓴이 = testMemberBuilder.builder()
                .id(1L)
                .email("email@email.com")
                .nickname("nickname")
                .buildWithoutSaving();
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이, PostImageInfos.create(), 0, 0, false);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        신고_서비스를_모킹한다(new Report(POST, 게시글.getId(), 글쓴이, AbusingType.OBSCENITY, ""));

        final ReportRequest reportRequest = new ReportRequest("POST", 게시글.getId(), AbusingType.OBSCENITY.getTypeCode(),
                "");

        final var 게시글_상세_조회_요청 = post("/report")
                .header("X-API-VERSION", "1.0.0", "1.1.0")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(reportRequest))
                .sessionAttr(USER.getSessionId(), 글쓴이.getId());

        final RestDocumentationResultHandler 문서화 = document("report-save",
                preprocessRequest(prettyPrint())
        );

        this.mockMvc.perform(게시글_상세_조회_요청)
                .andExpect(status().isCreated())
                .andDo(문서화);
    }
}
