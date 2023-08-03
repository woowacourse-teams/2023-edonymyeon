package edonymyeon.backend.member.application;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.application.MyPostService;
import edonymyeon.backend.post.application.PostConsumptionService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.application.dto.response.PostConsumptionResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostConsumptionService postConsumptionService;

    @InjectMocks
    private MyPostService myPostService;

    @Test
    void 내가_쓴글을_조회한다() {
        final GeneralFindingCondition findingCondition = GeneralFindingCondition.builder().build();
        final Pageable pageable = findingCondition.toPage();

        final Member 작성자 = new Member(1L);

        final List<Post> 게시글_목록 = 임의_게시글_목록(작성자);
        Slice<Post> 임의_반환_게시글_목록 = new SliceImpl<>(게시글_목록, pageable, true);

        when(postRepository.findAllByMemberId(anyLong(), any()))
                .thenReturn(임의_반환_게시글_목록);

        final Map<Long, PostConsumptionResponse> 소비확정_목록 = 임의_소비확정_목록(게시글_목록);

        when(postConsumptionService.findConsumptionsByPostIds(any())).thenReturn(소비확정_목록);

        final List<MyPostResponse> 반환결과 = myPostService.findMyPosts(new MemberIdDto(작성자.getId()), findingCondition)
                .getContent();

        assertSoftly(
                softly -> {
                    softly.assertThat(반환결과.size()).isEqualTo(게시글_목록.size());
                    softly.assertThat(반환결과.get(0).consumption().type()).isEqualTo(PURCHASE.name());
                    softly.assertThat(반환결과.get(1).consumption().type()).isEqualTo(PURCHASE.name());
                    softly.assertThat(반환결과.get(2).consumption().type()).isEqualTo("NONE");
                }
        );
    }

    List<Post> 임의_게시글_목록(final Member 작성자) {

        final Post 게시글1 = 게시글_생성(작성자, 1L);
        final Post 게시글2 = 게시글_생성(작성자, 2L);
        final Post 게시글3 = 게시글_생성(작성자, 3L);

        return List.of(게시글1, 게시글2, 게시글3);
    }

    Map<Long, PostConsumptionResponse> 임의_소비확정_목록(List<Post> 게시글_목록) {
        final Post 게시글1 = 게시글_목록.get(0);
        final Post 게시글2 = 게시글_목록.get(1);
        final Post 게시글3 = 게시글_목록.get(2);

        final Consumption 소비확정1 = Consumption.of(게시글1, PURCHASE, 1_000L, 2020, 1);
        final Consumption 소비확정2 = Consumption.of(게시글2, PURCHASE, 1_000L, 2020, 1);

        final PostConsumptionResponse none = PostConsumptionResponse.none();

        return Map.of(게시글1.getId(), from(소비확정1), 게시글2.getId(), from(소비확정2), 게시글3.getId(), none);
    }

    Post 게시글_생성(Member member, Long postId) {
        return new Post(postId, "title", "content", 1000L, member);
    }

    private PostConsumptionResponse from(Consumption consumption) {
        return new PostConsumptionResponse(
                consumption.getConsumptionType().name(),
                consumption.getPrice(),
                consumption.getConsumptionYear(),
                consumption.getConsumptionYear()
        );
    }
}
