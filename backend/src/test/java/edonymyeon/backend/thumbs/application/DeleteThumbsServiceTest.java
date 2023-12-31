package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_IS_NOT_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_IS_NOT_EXIST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.request.PostRequest;
import edonymyeon.backend.post.application.dto.response.PostIdResponse;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class DeleteThumbsServiceTest extends IntegrationFixture {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private PostIdResponse postIdResponse;

    @BeforeEach
    void 사전작업() {
        회원가입과_게시글쓰기를_한다();
    }

    void 회원가입과_게시글쓰기를_한다() {
        Member postWriter = registerMember();
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
    void 추천_취소(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();
        thumbsUp(otherMember, postIdResponse);

        // when
        deleteThumbsUp(otherMember, postIdResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postIdResponse.id(),
                otherMember.getId());

        // then
        Assertions.assertThat(postThumbs).isEmpty();
    }

    @Test
    void 추천이_되어있지_않은경우_추천취소를_할_수_없다() {
        Member otherMember = registerMember();

        assertThatThrownBy(() -> deleteThumbsUp(otherMember, postIdResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_UP_IS_NOT_EXIST.getMessage());
    }

    @Test
    void 비추천인_상태일때_추천을_취소할_수_없다() {
        Member otherMember = registerMember();
        thumbsDown(otherMember, postIdResponse);

        assertThatThrownBy(() -> deleteThumbsUp(otherMember, postIdResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN.getMessage());
    }

    @Test
    void 비추천_취소(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();
        thumbsDown(otherMember, postIdResponse);

        // when
        deleteThumbsDown(otherMember, postIdResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postIdResponse.id(),
                otherMember.getId());

        // then
        Assertions.assertThat(postThumbs).isEmpty();
    }

    @Test
    void 비추천이_되어있지_않은경우_비추천취소를_할_수_없다() {
        Member otherMember = registerMember();

        assertThatThrownBy(() -> deleteThumbsDown(otherMember, postIdResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_DOWN_IS_NOT_EXIST.getMessage());
    }

    @Test
    void 추천인_상태일때_비추천을_취소할_수_없다() {
        Member otherMember = registerMember();
        thumbsUp(otherMember, postIdResponse);

        assertThatThrownBy(() -> deleteThumbsDown(otherMember, postIdResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP.getMessage());
    }

    private void thumbsUp(final Member member, final PostIdResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.thumbsUp(memberId, post.id());
    }

    private void thumbsDown(final Member member, final PostIdResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.thumbsDown(memberId, post.id());
    }

    private void deleteThumbsUp(final Member member, final PostIdResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.deleteThumbsUp(memberId, post.id());
    }

    private void deleteThumbsDown(final Member member, final PostIdResponse post) {
        MemberId memberId = new ActiveMemberId(member.getId());
        thumbsService.deleteThumbsDown(memberId, post.id());
    }

    private Member registerMember() {
        Member member = memberTestSupport.builder().build();
        memberRepository.save(member);
        return member;
    }
}
