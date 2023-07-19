package edonymyeon.backend.thumbs.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
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

    private Member postWriter;

    private Member otherMember;

    private PostResponse postResponse;

    @BeforeEach
    public void 두_회원의_가입과_하나의_게시글쓰기를_한다() {
        postWriter = new Member(
                null,
                "email",
                "password",
                "nickname",
                "introduction",
                null
        );
        memberRepository.save(postWriter);

        otherMember = new Member(
                null,
                "otherEmail",
                "otherPassword",
                "otherNickname",
                "introduction",
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
        AllThumbsInPostResponse allThumbsInPostResponse = thumbsService.allThumbsInPost(postResponse.id());

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
                null,
                "otherEmail2",
                "otherPassword2",
                "otherNickname2",
                "introduction",
                null
        );
        memberRepository.save(otherMember2);

        // when
        MemberIdDto otherMemberId = new MemberIdDto(otherMember.getId());
        thumbsService.thumbsUp(otherMemberId, postResponse.id());

        MemberIdDto otherMember2Id = new MemberIdDto(otherMember2.getId());
        thumbsService.thumbsUp(otherMember2Id, postResponse.id());

        AllThumbsInPostResponse allThumbsInPostResponse = thumbsService.allThumbsInPost(postResponse.id());

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
}
