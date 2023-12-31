package edonymyeon.backend.thumbs.docs;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.member.domain.Email;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.TestMemberBuilder;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@IntegrationTest
class ThumbsControllerDocsTest {

    private final MockMvc mockMvc;

    private final TestMemberBuilder testMemberBuilder;

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
    void 사전작업() {
        회원_두명_가입하고_글쓰기_모킹();
    }

    void 회원_두명_가입하고_글쓰기_모킹() {
        글쓴이 = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();

        when(memberRepository.findByEmail(Email.from(글쓴이.getEmail()))).thenReturn(Optional.of(글쓴이));
        when(memberRepository.findById(글쓴이.getId())).thenReturn(Optional.of(글쓴이));

        반응_하는_사람 = testMemberBuilder.builder()
                .id(2L)
                .buildWithoutSaving();

        when(memberRepository.findByEmail(Email.from(반응_하는_사람.getEmail()))).thenReturn(
                Optional.of(반응_하는_사람));
        when(memberRepository.findById(반응_하는_사람.getId())).thenReturn(Optional.of(반응_하는_사람));

        글 = new Post(1L, "title", "content", 1_000L, 글쓴이);
        when(postRepository.findById(글.getId())).thenReturn(Optional.of(글));
    }

    @Test
    void 게시글에_좋아요를_한다() throws Exception {
        Thumbs 조와요 = new Thumbs(글, 글쓴이, ThumbsType.UP);
        when(thumbsRepository.save(조와요)).thenReturn(조와요);

        final MockHttpServletRequestBuilder 좋아요_요청 = put("/posts/{postId}/up", 글.getId())
                .sessionAttr(USER.getSessionId(), 반응_하는_사람.getId())
                .header("X-API-VERSION", "1.0.0", "1.1.0");

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
                .sessionAttr(USER.getSessionId(), 반응_하는_사람.getId())
                .header("X-API-VERSION", "1.0.0", "1.1.0");

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
                .sessionAttr(USER.getSessionId(), 반응_하는_사람.getId())
                .header("X-API-VERSION", "1.0.0", "1.1.0");

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
                .sessionAttr(USER.getSessionId(), 반응_하는_사람.getId())
                .header("X-API-VERSION", "1.0.0", "1.1.0");

        this.mockMvc.perform(싫어요_취소_요청)
                .andExpect(status().isOk())
                .andDo(document("thumbs-down-delete",
                        pathParameters(
                                parameterWithName("postId").description("게시글 id")
                        )
                ));
    }
}
