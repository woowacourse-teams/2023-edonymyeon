package edonymyeon.backend.content.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_ALREADY_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_IS_SELF_UP_DOWN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.profile.application.dto.ActiveMemberId;
import edonymyeon.backend.member.profile.application.dto.MemberId;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.member.profile.repository.MemberRepository;
import edonymyeon.backend.content.post.application.PostService;
import edonymyeon.backend.content.post.application.dto.request.PostRequest;
import edonymyeon.backend.content.post.application.dto.response.PostIdResponse;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.content.thumbs.domain.Thumbs;
import edonymyeon.backend.content.thumbs.domain.ThumbsType;
import edonymyeon.backend.content.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class ThumbsDownServiceTest extends IntegrationFixture {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private Member postWriter;

    private PostIdResponse postIdResponse;

    @BeforeEach
    void 사전작업() {
        회원가입과_게시글쓰기를_한다();
    }

    public void 회원가입과_게시글쓰기를_한다() {
        postWriter = registerMember();
        PostRequest postRequest = new PostRequest(
                "title",
                "content",
                1000L,
                null
        );

        MemberId memberId = new ActiveMemberId(postWriter.getId());
        postIdResponse = postService.createPost(memberId, postRequest);
    }

    @Test
    void 비추천하려는_게시물이_없으면_예외가_발생한다() {
        MemberId loginMemberId = new ActiveMemberId(postWriter.getId());

        assertThatThrownBy(
                () -> thumbsService.thumbsDown(loginMemberId, 1000L))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(POST_ID_NOT_FOUND.getMessage());
    }

    @Test
    void 비추천한_게시물이_로그인한_사람이_작성한_것이라면_예외가_발생한다() {
        MemberId loginMemberId = new ActiveMemberId(postWriter.getId());

        assertThatThrownBy(
                () -> thumbsService.thumbsDown(loginMemberId, postIdResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_IS_SELF_UP_DOWN.getMessage());
    }

    @Test
    void 비추천시_해당게시물에_비추천한_적_없으면_추가한다(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();

        // when
        thumbsDown(otherMember, postIdResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postIdResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.DOWN);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postIdResponse.id());
                }
        );
    }

    @Test
    void 비추천시_해당게시물에_추천한_적_있으면_비추천으로_업데이트_된다(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();

        // when
        thumbsUp(otherMember, postIdResponse);
        thumbsDown(otherMember, postIdResponse);

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postIdResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.DOWN);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postIdResponse.id());
                }
        );
    }

    @Test
    void 비추천시_해당게시물에_이미_비추천되어_있으면_예외가_발생한다() {
        // given
        Member otherMember = registerMember();

        // when
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());
        thumbsDown(otherMember, postIdResponse);

        assertThatThrownBy(
                () -> thumbsService.thumbsDown(otherMemberId, postIdResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_DOWN_ALREADY_EXIST.getMessage());
    }

    private void thumbsUp(final Member member, final PostIdResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.thumbsUp(memberId, post.id());
    }

    private void thumbsDown(final Member member, final PostIdResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.thumbsDown(memberId, post.id());
    }

    private Member registerMember() {
        Member member = memberTestSupport.builder().build();
        memberRepository.save(member);
        return member;
    }
}
