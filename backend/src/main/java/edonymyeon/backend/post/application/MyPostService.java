package edonymyeon.backend.post.application;

import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.dto.response.MyPostResponse;
import edonymyeon.backend.post.application.dto.response.PostConsumptionResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MyPostService {

    private final PostRepository postRepository;

    private final PostConsumptionService postConsumptionService;

    private final Domain domain;

    public Slice<MyPostResponse> findMyPosts(final MemberId memberIdDto,
                                             final GeneralFindingCondition findingCondition) {
        final Slice<Post> posts = postRepository.findAllByMemberId(memberIdDto.id(), findingCondition.toPage());
        final List<Long> postIds = posts.map(Post::getId).toList();
        final Map<Long, PostConsumptionResponse> consumptionsByPostId = postConsumptionService.findConsumptionsByPostIds(
                postIds);

        return posts.map(post -> MyPostResponse.of(post, domain, consumptionsByPostId.get(post.getId())));
    }
}
