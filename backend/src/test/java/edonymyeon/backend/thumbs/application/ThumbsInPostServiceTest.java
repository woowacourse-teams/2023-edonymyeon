package edonymyeon.backend.thumbs.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.dto.ThumbsStatusInPostResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
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
public class ThumbsInPostServiceTest {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private Member otherMember;

    private PostResponse postResponse;

    @BeforeEach
    public void 두_회원의_가입과_하나의_게시글쓰기를_한다() {
        Member postWriter = new Member(
                "email",
                "password",
                "nickname",
                null
        );
        memberRepository.save(postWriter);

        otherMember = new Member(
                "otherEmail",
                "otherPassword",
                "otherNickname",
                null
        );
        memberRepository.save(otherMember);

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
    void 해당_게시글에_추천이_없을시에도_정상_동작한다() {
        AllThumbsInPostResponse allThumbsInPostResponse = thumbsService.findAllThumbsInPost(postResponse.id());

        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPostResponse.thumbsUpCount()).isEqualTo(0);
                    softly.assertThat(allThumbsInPostResponse.thumbsDownCount()).isEqualTo(0);
                }
        );
    }

    @Test
    void 해당_게시글에_추천과_비추천_수를_읽어온다() {
        // given
        Member otherMember2 = new Member(
                "otherEmail2",
                "otherPassword2",
                "otherNickname2",
                null
        );
        memberRepository.save(otherMember2);

        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());
        thumbsService.thumbsUp(otherMemberId, postResponse.id());

        MemberIdDto otherMember2Id = new MemberIdDto(otherMember2.getId());
        thumbsService.thumbsUp(otherMember2Id, postResponse.id());

        // when
        AllThumbsInPostResponse allThumbsInPostResponse = thumbsService.findAllThumbsInPost(postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPostResponse.thumbsUpCount()).isEqualTo(2);
                    softly.assertThat(allThumbsInPostResponse.thumbsDownCount()).isEqualTo(0);
                }
        );
    }

    @Test
    void 해당_게시글에_추천과_비추천_수를_읽어온다2() {
        // TODO: 비추 기능 추가되면 테스트 케이스 추가하기
    }

    @Test
    void 로그인_하지_않으면_추천과_비추천_여부가_모두_false_이다() {
        // given
        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());
        thumbsService.thumbsUp(otherMemberId, postResponse.id());

        // when
        MemberIdDto emptyId = new MemberIdDto(null);
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = thumbsService.findThumbsStatusInPost(emptyId,
                postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsStatusInPostResponse.isUp()).isFalse();
                    softly.assertThat(thumbsStatusInPostResponse.isDown()).isFalse();
                }
        );
    }

    @Test
    void 해당_게시글을_추천했을때_추천여부만_True_이다() {
        // given
        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());
        thumbsService.thumbsUp(otherMemberId, postResponse.id());

        // when
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = thumbsService.findThumbsStatusInPost(otherMemberId,
                postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsStatusInPostResponse.isUp()).isTrue();
                    softly.assertThat(thumbsStatusInPostResponse.isDown()).isFalse();
                }
        );
    }

    @Test
    void 해당_게시글을_추천하지_않았을때_모두_False_이다() {
        // given
        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());

        // when
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = thumbsService.findThumbsStatusInPost(otherMemberId,
                postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsStatusInPostResponse.isUp()).isFalse();
                    softly.assertThat(thumbsStatusInPostResponse.isDown()).isFalse();
                }
        );
    }

    @Test
    void 해당_게시글의_비추천여부를_알아본다() {
        // TODO: 비추 기능 추가되면 테스트 케이스 추가하기
    }
}
