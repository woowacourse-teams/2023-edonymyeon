package edonymyeon.backend.thumbs.application;

import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.PostThumbsService;
import edonymyeon.backend.post.application.dto.AllThumbsInPostResponse;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.application.dto.ThumbsStatusInPostResponse;
import edonymyeon.backend.support.MemberTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
@SpringBootTest
public class ThumbsInPostServiceTest {

    private final PostThumbsService postThumbsService;

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private final MemberTestSupport memberTestSupport;

    private Member otherMember;

    private PostResponse postResponse;

    @BeforeEach
    public void 두_회원의_가입과_하나의_게시글쓰기를_한다() {
        otherMember = registerMember();
        Member postWriter = registerMember();

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
    void 해당_게시글에_추천이_없을시에도_정상_동작한다() {
        AllThumbsInPostResponse allThumbsInPostResponse = postThumbsService.findAllThumbsInPost(postResponse.id());

        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPostResponse.thumbsUpCount()).isEqualTo(0);
                    softly.assertThat(allThumbsInPostResponse.thumbsDownCount()).isEqualTo(0);
                }
        );
    }

    @Test
    void 해당_게시글에_추천과_비추천_수를_읽어온다() {
        // given
        Member otherMember2 = registerMember();
        thumbsUp(otherMember, postResponse);
        thumbsUp(otherMember2, postResponse);

        // when
        AllThumbsInPostResponse allThumbsInPostResponse = postThumbsService.findAllThumbsInPost(postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPostResponse.thumbsUpCount()).isEqualTo(2);
                    softly.assertThat(allThumbsInPostResponse.thumbsDownCount()).isEqualTo(0);
                }
        );
    }

    @Test
    void 해당_게시글에_추천과_비추천_수를_읽어온다2() {
        // given
        Member otherMember2 = registerMember();
        Member otherMember3 = registerMember();

        thumbsUp(otherMember, postResponse);
        thumbsUp(otherMember2, postResponse);
        thumbsDown(otherMember3, postResponse);

        // when
        AllThumbsInPostResponse allThumbsInPostResponse = postThumbsService.findAllThumbsInPost(postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(allThumbsInPostResponse.thumbsUpCount()).isEqualTo(2);
                    softly.assertThat(allThumbsInPostResponse.thumbsDownCount()).isEqualTo(1);
                }
        );
    }

    @Test
    void 로그인_하지_않으면_추천과_비추천_여부가_모두_false_이다() {
        // given
        thumbsUp(otherMember, postResponse);

        // when
        MemberId emptyId = new ActiveMemberId(AnonymousMemberId.ANONYMOUS_MEMBER_ID);
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(emptyId,
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
        thumbsUp(otherMember, postResponse);

        // when
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(otherMemberId,
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
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());

        // when
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(otherMemberId,
                postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsStatusInPostResponse.isUp()).isFalse();
                    softly.assertThat(thumbsStatusInPostResponse.isDown()).isFalse();
                }
        );
    }

    @Test
    void 해당_게시글을_비추천했을때_비추천여부만_True_이다() {
        // given
        thumbsDown(otherMember, postResponse);

        // when
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(otherMemberId,
                postResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsStatusInPostResponse.isUp()).isFalse();
                    softly.assertThat(thumbsStatusInPostResponse.isDown()).isTrue();
                }
        );
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
