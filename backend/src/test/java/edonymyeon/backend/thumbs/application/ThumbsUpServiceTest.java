package edonymyeon.backend.thumbs.application;

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
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static edonymyeon.backend.global.exception.ExceptionInformation.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class ThumbsUpServiceTest extends IntegrationFixture {

    private final ThumbsService thumbsService;

    private final MemberRepository memberRepository;

    private final PostService postService;

    private Member postWriter;

    private PostIdResponse postIdResponse;

    @BeforeEach
    void 사전작업() {
        회원가입과_게시글쓰기를_한다();
    }

    void 회원가입과_게시글쓰기를_한다() {
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
    void 추천하려는_게시물이_없으면_예외가_발생한다() {
        MemberId loginMemberId = new ActiveMemberId(postWriter.getId());

        assertThatThrownBy(
                () -> thumbsService.thumbsUp(loginMemberId, 1000L))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(POST_ID_NOT_FOUND.getMessage());
    }

    @Test
    void 추천한_게시물이_로그인한_사람이_작성한_것이라면_예외가_발생한다() {
        MemberId loginMemberId = new ActiveMemberId(postWriter.getId());

        assertThatThrownBy(
                () -> thumbsService.thumbsUp(loginMemberId, postIdResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_IS_SELF_UP_DOWN.getMessage());
    }

    @Test
    void 추천시_해당게시물에_추천한_적_없으면_추가한다(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();

        // when
        thumbsUp(otherMember, postIdResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postIdResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.UP);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postIdResponse.id());
                }
        );
    }

    @Test
    void 추천시_해당게시물에_비추천한_적_있으면_추천으로_업데이트_된다(@Autowired ThumbsRepository thumbsRepository) {
        // given
        Member otherMember = registerMember();

        // when
        thumbsDown(otherMember, postIdResponse);
        thumbsUp(otherMember, postIdResponse);
        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postIdResponse.id(),
                otherMember.getId());

        // then
        assertSoftly(softly -> {
                    softly.assertThat(postThumbs).isPresent();
                    softly.assertThat(postThumbs.get().getThumbsType()).isEqualTo(ThumbsType.UP);
                    softly.assertThat(postThumbs.get().getMember().getId()).isEqualTo(otherMember.getId());
                    softly.assertThat(postThumbs.get().getPost().getId()).isEqualTo(postIdResponse.id());
                }
        );
    }

    @Test
    void 추천시_해당게시물에_추천한_적_있으면_예외가_발생한다() {
        // given
        Member otherMember = registerMember();

        // when
        thumbsUp(otherMember, postIdResponse);

        // then
        MemberId otherMemberId = new ActiveMemberId(otherMember.getId());

        assertThatThrownBy(
                () -> thumbsService.thumbsUp(otherMemberId, postIdResponse.id()))
                .isExactlyInstanceOf(EdonymyeonException.class)
                .hasMessage(THUMBS_UP_ALREADY_EXIST.getMessage());
    }

    @Test
    void 같은_사람이_같은_게시글에_동시에_여러번_좋아요를_누르더라도_한번만_저장된다() throws InterruptedException {
        // given
        int numberOfExecute = 10;
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfExecute);

        Member otherMember = registerMember();

        // when
        for (int i = 0; i < numberOfExecute; i++) {
            service.execute(() -> {
                try {
                    thumbsService.thumbsUp(new ActiveMemberId(otherMember.getId()), postIdResponse.id());
                    successCount.getAndIncrement();
                    System.out.println("성공");
                } catch (EdonymyeonException e) {
                    failCount.getAndIncrement();
                    System.out.println("충돌감지");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                latch.countDown();
            });
        }
        latch.await();

        // then
        assertSoftly(softly -> {
                    softly.assertThat(successCount.get()).isEqualTo(1);
                    softly.assertThat(failCount.get()).isEqualTo(9);
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
