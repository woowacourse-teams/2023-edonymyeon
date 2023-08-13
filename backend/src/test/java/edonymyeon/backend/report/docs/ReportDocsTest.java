package edonymyeon.backend.report.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.report.application.ReportRepository;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.report.domain.AbusingType;
import edonymyeon.backend.report.domain.Report;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
public class ReportDocsTest implements ImageFileCleaner {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private ReportRepository reportRepository;

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

    private void 신고_서비스를_모킹한다(final Report 신고) {
        when(reportRepository.save(any()))
                .thenReturn(new Report(1L, 신고.getPost(), 신고.getPost().getMember(), AbusingType.OBSCENITY, ""));
    }

    @Test
    void 게시글을_상세_조회한다() throws Exception {
        final Member 글쓴이 = new Member(1L, "email", "password", "nickname", null, null);
        final Post 게시글 = new Post(1L, "제목", "내용", 1000L, 글쓴이, PostImageInfos.create(), 0);

        회원_레포지토리를_모킹한다(글쓴이);
        게시글_레포지토리를_모킹한다(게시글);
        신고_서비스를_모킹한다(new Report(null, 게시글, 글쓴이, AbusingType.OBSCENITY, ""));

        final ReportRequest reportRequest = new ReportRequest(게시글.getId(), AbusingType.OBSCENITY.getTypeCode(), "");

        final var 게시글_상세_조회_요청 = post("/report")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(reportRequest))
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((글쓴이.getEmail() + ":" + 글쓴이.getPassword()).getBytes()));

        final RestDocumentationResultHandler 문서화 = document("report-save",
                preprocessRequest(prettyPrint())
        );

        this.mockMvc.perform(게시글_상세_조회_요청)
                .andExpect(status().isCreated())
                .andDo(문서화);
    }
}
