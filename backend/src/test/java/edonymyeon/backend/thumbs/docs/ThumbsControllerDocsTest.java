package edonymyeon.backend.thumbs.docs;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest
public class ThumbsControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private ThumbsRepository thumbsRepository;

    private Member 글쓴이;
    private Member 반응_하는_사람;
    private Post 글;

    @BeforeEach
    void 회원_두명_가입하고_글쓰기_모킹() {
        글쓴이 = new Member(1L, "email", "password", "nickname", null);
        when(memberRepository.findByEmailAndPassword(글쓴이.getEmail(), 글쓴이.getPassword())).thenReturn(Optional.of(글쓴이));
        when(memberRepository.findById(글쓴이.getId())).thenReturn(Optional.of(글쓴이));

        반응_하는_사람 = new Member(2L, "email2", "password2", "nickname2", null);
        when(memberRepository.findByEmailAndPassword(반응_하는_사람.getEmail(), 반응_하는_사람.getPassword())).thenReturn(
                Optional.of(반응_하는_사람));
        when(memberRepository.findById(반응_하는_사람.getId())).thenReturn(Optional.of(반응_하는_사람));

        글 = new Post(1L, "title", "content", 1_000L, 글쓴이, null, LocalDateTime.now(), 0L);
        when(postRepository.findById(글.getId())).thenReturn(Optional.of(글));
    }

    @Test
    void 게시글에_좋아요를_한다() throws Exception {
        Thumbs 조와요 = new Thumbs(글, 글쓴이, ThumbsType.UP);
        when(thumbsRepository.save(조와요)).thenReturn(조와요);

        final MockHttpServletRequestBuilder 좋아요_요청 = put("/posts/{postId}/up", 글.getId())
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((반응_하는_사람.getEmail() + ":" + 반응_하는_사람.getPassword()).getBytes()));

        this.mockMvc.perform(좋아요_요청)
                .andExpect(status().isOk())
                .andDo(document("thumbs-up",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        )
                ));
    }

    @Test
    void 게시글에_싫어요를_한다() throws Exception {
        Thumbs 시러요 = new Thumbs(글, 글쓴이, ThumbsType.DOWN);
        when(thumbsRepository.save(시러요)).thenReturn(시러요);

        final MockHttpServletRequestBuilder 싫어요_요청 = put("/posts/{postId}/down", 글.getId())
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((반응_하는_사람.getEmail() + ":" + 반응_하는_사람.getPassword()).getBytes()));

        this.mockMvc.perform(싫어요_요청)
                .andExpect(status().isOk())
                .andDo(document("thumbs-down",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        )
                ));
    }

    @Test
    void 게시글에_좋아요를_취소_한다() throws Exception {
        Thumbs 좋아요 = new Thumbs(글, 글쓴이, ThumbsType.UP);
        when(thumbsRepository.findByPostIdAndMemberId(글.getId(), 반응_하는_사람.getId())).thenReturn(Optional.of(좋아요));

        final MockHttpServletRequestBuilder 좋아요_취소_요청 = delete("/posts/{postId}/up", 글.getId())
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((반응_하는_사람.getEmail() + ":" + 반응_하는_사람.getPassword()).getBytes()));

        this.mockMvc.perform(좋아요_취소_요청)
                .andExpect(status().isOk())
                .andDo(document("thumbs-up-delete",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        )
                ));
    }

    @Test
    void 게시글에_싫어요를_취소_한다() throws Exception {
        Thumbs 싫어요 = new Thumbs(글, 글쓴이, ThumbsType.DOWN);
        when(thumbsRepository.findByPostIdAndMemberId(글.getId(), 반응_하는_사람.getId())).thenReturn(Optional.of(싫어요));

        final MockHttpServletRequestBuilder 싫어요_취소_요청 = delete("/posts/{postId}/down", 글.getId())
                .header(HttpHeaders.AUTHORIZATION, "Basic "
                        + java.util.Base64.getEncoder()
                        .encodeToString((반응_하는_사람.getEmail() + ":" + 반응_하는_사람.getPassword()).getBytes()));

        this.mockMvc.perform(싫어요_취소_요청)
                .andExpect(status().isOk())
                .andDo(document("thumbs-down-delete",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        )
                ));
    }
}
