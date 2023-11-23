package edonymyeon.backend.thumbs.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.content.thumbs.application.ThumbsService;
import edonymyeon.backend.membber.member.application.dto.ActiveMemberId;
import edonymyeon.backend.membber.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.membber.member.application.dto.MemberId;
import edonymyeon.backend.membber.member.domain.Member;
import edonymyeon.backend.membber.member.repository.MemberRepository;
import edonymyeon.backend.content.post.application.PostService;
import edonymyeon.backend.content.post.application.PostThumbsService;
import edonymyeon.backend.content.post.application.dto.response.AllThumbsInPostResponse;
import edonymyeon.backend.content.post.application.dto.request.PostRequest;
import edonymyeon.backend.content.post.application.dto.response.PostIdResponse;
import edonymyeon.backend.content.post.application.dto.response.ThumbsStatusInPostResponse;
import edonymyeon.backend.support.IntegrationFixture;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class ThumbsInPostServiceTest extends IntegrationFixture {

    private final PostThumbsService postThumbsService;

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private Member otherMember;

    private PostIdResponse postIdResponse;

    @BeforeEach
    public void 사전작업() {
        두_회원의_가입과_하나의_게시글쓰기를_한다();
    }

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
        postIdResponse = postService.createPost(memberId, postRequest);
    }

    @Test
    void 해당_게시글에_추천이_없을시에도_정상_동작한다() {
        AllThumbsInPostResponse allThumbsInPostResponse = postThumbsService.findAllThumbsInPost(postIdResponse.id());

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
        thumbsUp(otherMember, postIdResponse);
        thumbsUp(otherMember2, postIdResponse);

        // when
        AllThumbsInPostResponse allThumbsInPostResponse = postThumbsService.findAllThumbsInPost(postIdResponse.id());

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

        thumbsUp(otherMember, postIdResponse);
        thumbsUp(otherMember2, postIdResponse);
        thumbsDown(otherMember3, postIdResponse);

        // when
        AllThumbsInPostResponse allThumbsInPostResponse = postThumbsService.findAllThumbsInPost(postIdResponse.id());

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
        thumbsUp(otherMember, postIdResponse);

        // when
        MemberId emptyId = new ActiveMemberId(AnonymousMemberId.ANONYMOUS_MEMBER_ID);
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(emptyId,
                postIdResponse.id());

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
        thumbsUp(otherMember, postIdResponse);

        // when
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(otherMemberId,
                postIdResponse.id());

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
                postIdResponse.id());

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
        thumbsDown(otherMember, postIdResponse);

        // when
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());
        ThumbsStatusInPostResponse thumbsStatusInPostResponse = postThumbsService.findThumbsStatusInPost(otherMemberId,
                postIdResponse.id());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(thumbsStatusInPostResponse.isUp()).isFalse();
                    softly.assertThat(thumbsStatusInPostResponse.isDown()).isTrue();
                }
        );
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
