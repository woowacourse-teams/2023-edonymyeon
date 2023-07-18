package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_ALREADY_EXIST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@SpringBootTest
class ThumbsServiceTest {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final ThumbsRepository thumbsRepository;

    private final PostService postService;

    private Member postWriter;

    private PostResponse postResponse;

    @BeforeEach
    public void 회원가입과_게시글쓰기를_한다() {
        postWriter = new Member(
                null,
                "email",
                "password",
                "nickname",
                "introduction",
                null
        );
        memberRepository.save(postWriter);

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
    void 추천하려는_게시물이_없으면_예외가_발생한다() {
        MemberIdDto loginMemberId = new MemberIdDto(postWriter.getId());

        // TODO: 없는 게시글 조회 예외 종류 추가 해주기
        assertThatThrownBy(
                () -> thumbsService.thumbsUp(loginMemberId, 1000L))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 추천한_게시물이_로그인한_사람이_작성한_것이라면_예외가_발생한다() {
        MemberIdDto loginMemberId = new MemberIdDto(postWriter.getId());

        assertThatThrownBy(
                () -> thumbsService.thumbsUp(loginMemberId, postResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class);
    }

    @Test
    void 추천시_해당게시물에_추천한_적_없으면_추가한다() {
        // given
        Member otherMember = new Member(
                null,
                "email2",
                "password2",
                "nickname2",
                "introduction2",
                null
        );
        memberRepository.save(otherMember);

        // when
        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());
        thumbsService.thumbsUp(otherMemberId, postResponse.id());

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.UP);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postResponse.id());
                }
        );
    }

    @Test
    void 추천시_해당게시물에_비추천한_적_있으면_추천으로_업데이트_된다() {
        // TODO : 비추 구현시 구현
    }

    @Test
    void 추천시_해당게시물에_추천한_적_있으면_예외가_발생한다() {
        // given
        Member otherMember = new Member(
                null,
                "email2",
                "password2",
                "nickname2",
                "introduction2",
                null
        );
        memberRepository.save(otherMember);

        // when
        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());
        thumbsService.thumbsUp(otherMemberId, postResponse.id());

        assertThatThrownBy(
                () -> thumbsService.thumbsUp(otherMemberId, postResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_UP_ALREADY_EXIST.getMessage());
    }
}
