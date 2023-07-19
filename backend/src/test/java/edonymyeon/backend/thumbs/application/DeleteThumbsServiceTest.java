package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_IS_NOT_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_IS_NOT_EXIST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
@SpringBootTest
public class DeleteThumbsServiceTest {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private Member postWriter;

    private PostResponse postResponse;

    @BeforeEach
    public void 회원가입과_게시글쓰기를_한다() {
        postWriter = registerMember("email", "password", "nickname");
        PostRequest postRequest = new PostRequest(
                "title",
                "content",
                1000L,
                null
        );

        MemberIdDto memberId = new MemberIdDto(postWriter.getId());
        postResponse = postService.createPost(memberId, postRequest);
    }

    @Test
    void 추천_취소(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember("email2", "password2", "nickname2");
        thumbsUp(otherMember, postResponse);

        // when
        deleteThumbsUp(otherMember, postResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postResponse.id(),
                otherMember.getId());

        // then
        Assertions.assertThat(postThumbs).isEmpty();
    }

    @Test
    void 추천이_되어있지_않은경우_추천취소를_할_수_없다() {
        Member otherMember = registerMember("email2", "password2", "nickname2");

        assertThatThrownBy(() -> deleteThumbsUp(otherMember, postResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_UP_IS_NOT_EXIST.getMessage());
    }

    @Test
    void 비추천인_상태일때_추천을_취소할_수_없다() {
        Member otherMember = registerMember("email2", "password2", "nickname2");
        thumbsDown(otherMember, postResponse);

        assertThatThrownBy(() -> deleteThumbsUp(otherMember, postResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN.getMessage());
    }

    @Test
    void 비추천_취소(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember("email2", "password2", "nickname2");
        thumbsDown(otherMember, postResponse);

        // when
        deleteThumbsDown(otherMember, postResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postResponse.id(),
                otherMember.getId());

        // then
        Assertions.assertThat(postThumbs).isEmpty();
    }

    @Test
    void 비추천이_되어있지_않은경우_비추천취소를_할_수_없다() {
        Member otherMember = registerMember("email2", "password2", "nickname2");

        assertThatThrownBy(() -> deleteThumbsDown(otherMember, postResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_DOWN_IS_NOT_EXIST.getMessage());
    }

    @Test
    void 추천인_상태일때_비추천을_취소할_수_없다() {
        Member otherMember = registerMember("email2", "password2", "nickname2");
        thumbsUp(otherMember, postResponse);

        assertThatThrownBy(() -> deleteThumbsDown(otherMember, postResponse))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP.getMessage());
    }

    private void thumbsUp(final Member member, final PostResponse post) {
        MemberIdDto memberId = new MemberIdDto(member.getId());
        thumbsService.thumbsUp(memberId, post.id());
    }

    private void thumbsDown(final Member member, final PostResponse post) {
        MemberIdDto memberId = new MemberIdDto(member.getId());
        thumbsService.thumbsDown(memberId, post.id());
    }

    private void deleteThumbsUp(final Member member, final PostResponse post) {
        MemberIdDto memberId = new MemberIdDto(member.getId());
        thumbsService.deleteThumbsUp(memberId, post.id());
    }

    private void deleteThumbsDown(final Member member, final PostResponse post) {
        MemberIdDto memberId = new MemberIdDto(member.getId());
        thumbsService.deleteThumbsDown(memberId, post.id());
    }

    private Member registerMember(
            final String email,
            final String password,
            final String nickname
    ) {
        Member member = new Member(
                email,
                password,
                nickname,
                null
        );
        memberRepository.save(member);

        return member;
    }
}
