package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_ALREADY_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_IS_SELF_UP_DOWN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
public class ThumbsDownServiceTest {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private final MemberTestSupport memberTestSupport;

    private Member postWriter;

    private PostResponse postResponse;

    @BeforeEach
    public void 회원가입과_게시글쓰기를_한다() {
        postWriter = registerMember();
        PostRequest postRequest = new PostRequest(
                "title",
                "content",
                1000L,
                null
        );

        MemberId memberId = new ActiveMemberId(postWriter.getId());
        postResponse = postService.createPost(memberId, postRequest);
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
                () -> thumbsService.thumbsDown(loginMemberId, postResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_IS_SELF_UP_DOWN.getMessage());
    }

    @Test
    void 비추천시_해당게시물에_비추천한_적_없으면_추가한다(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();

        // when
        thumbsDown(otherMember, postResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.DOWN);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postResponse.id());
                }
        );
    }

    @Test
    void 비추천시_해당게시물에_추천한_적_있으면_비추천으로_업데이트_된다(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();

        // when
        thumbsUp(otherMember, postResponse);
        thumbsDown(otherMember, postResponse);

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.DOWN);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postResponse.id());
                }
        );
    }

    @Test
    void 비추천시_해당게시물에_이미_비추천되어_있으면_예외가_발생한다() {
        // given
        Member otherMember = registerMember();

        // when
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());
        thumbsDown(otherMember, postResponse);

        assertThatThrownBy(
                () -> thumbsService.thumbsDown(otherMemberId, postResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_DOWN_ALREADY_EXIST.getMessage());
    }

    private void thumbsUp(final Member member, final PostResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.thumbsUp(memberId, post.id());
    }

    private void thumbsDown(final Member member, final PostResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.thumbsDown(memberId, post.id());
    }

    private Member registerMember() {
        Member member = memberTestSupport.builder().build();
        memberRepository.save(member);
        return member;
    }
}
