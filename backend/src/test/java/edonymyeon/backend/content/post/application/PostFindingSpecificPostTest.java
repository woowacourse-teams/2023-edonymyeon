package edonymyeon.backend.content.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.support.TestMemberBuilder;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.TestConfig;
import edonymyeon.backend.member.profile.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.member.profile.profileimage.repository.ProfileImageInfoRepository;
import edonymyeon.backend.member.profile.application.dto.ActiveMemberId;
import edonymyeon.backend.member.profile.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.profile.application.dto.MemberId;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.member.profile.repository.MemberRepository;
import edonymyeon.backend.content.post.domain.Post;
import edonymyeon.backend.content.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@Import(TestConfig.class)
@IntegrationTest
public class PostFindingSpecificPostTest {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final ProfileImageInfoRepository profileImageInfoRepository;

    private final TestMemberBuilder testMemberBuilder;

    private final PostReadService postReadService;

    private MemberId memberId;

    private MemberId member2Id;

    private Long postId;

    @BeforeEach
    void setUp() {
        final Member member = saveMember();
        saveMember2();
        savePost(member);
    }

    @Test
    void 게시글아이디가_주어지면_게시글의_상세정보를_알려준다() {
        final var postInfoResponse = postReadService.findSpecificPost(postId, memberId);

        assertAll(
                () -> assertThat(postInfoResponse.title()).isEqualTo("Summer Breeze"),
                () -> assertThat(postInfoResponse.content()).isEqualTo(
                        "Enjoy the refreshing summer breeze and soak up the sun's warmth at the beach."),

                () -> assertThat(postInfoResponse.price()).isEqualTo(14_000L),
                () -> assertThat(postInfoResponse.createdAt()).isNotNull(),
                () -> assertThat(postInfoResponse.upCount()).isEqualTo(0),
                () -> assertThat(postInfoResponse.downCount()).isEqualTo(0),

                () -> assertThat(postInfoResponse.isUp()).isFalse(),
                () -> assertThat(postInfoResponse.isDown()).isFalse(),
                () -> assertThat(postInfoResponse.isWriter()).isTrue()
        );
    }

    @Test
    void 로그인_되어있지_않으면_조회는_가능하되_추천여부와_스크랩여부와_작성자여부는_모두_false이다() {
        final var postInfoResponse = postReadService.findSpecificPost(postId, new AnonymousMemberId());

        assertThat(postInfoResponse.isUp()).isFalse();
        assertThat(postInfoResponse.isDown()).isFalse();
        assertThat(postInfoResponse.isWriter()).isFalse();
    }

    @Test
    void 작성자_본인이_본인_게시글을_보는경우_isWriter값이_true이다() {
        final var postInfoResponse = postReadService.findSpecificPost(postId, memberId);

        assertThat(postInfoResponse.isWriter()).isTrue();
    }

    @Test
    void 타인의_게시글을_보는경우_isWriter값이_false이다() {
        final var postInfoResponse = postReadService.findSpecificPost(postId, member2Id);

        assertThat(postInfoResponse.isWriter()).isFalse();
    }

    private void savePost(final Member member) {
        final var post = new Post(
                "Summer Breeze",
                "Enjoy the refreshing summer breeze and soak up the sun's warmth at the beach.",
                14_000L,
                member
        );
        postId = postRepository.save(post).getId();
    }

    private Member saveMember() {
        final Member member = testMemberBuilder.builder()
                .profileImageInfo(saveProfileImageInfo())
                .build();
        memberId = new ActiveMemberId(member.getId());
        return member;
    }

    private Member saveMember2() {
        final Member member = testMemberBuilder.builder()
                .profileImageInfo(saveProfileImageInfo2())
                .build();
        member2Id = new ActiveMemberId(memberRepository.save(member).getId());
        return member;
    }

    private ProfileImageInfo saveProfileImageInfo() {
        final ImageInfo imageInfo = new ImageInfo("test_image_1.jpg");
        return profileImageInfoRepository.save(
                ProfileImageInfo.from(imageInfo));
    }

    private ProfileImageInfo saveProfileImageInfo2() {
        final ImageInfo imageInfo = new ImageInfo("test_image_2.jpg");
        return profileImageInfoRepository.save(
                ProfileImageInfo.from(imageInfo));
    }
}
