package edonymyeon.backend.post.application;

import edonymyeon.backend.cache.BooleanCacheService;
import edonymyeon.backend.cache.LongCacheService;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.*;
import edonymyeon.backend.post.domain.HotPostPolicy;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.post.repository.PostSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static org.springframework.data.domain.Sort.Direction;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReadService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final PostThumbsService thumbsService;

    private final Domain domain;

    public PostSlice<GeneralPostInfoResponse> findPostsByPagingCondition(
            private final BooleanCacheService booleanCacheService;

    private final LongCacheService longCacheService;

    public Slice<GeneralPostInfoResponse> findPostsByPagingCondition(
            final GeneralFindingCondition generalFindingCondition) {
        PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        Slice<GeneralPostInfoResponse> posts = postRepository.findAllBy(pageRequest)
                .map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()));
        return PostSlice.from(posts);
    }

    private static PageRequest convertConditionToPageRequest(final GeneralFindingCondition generalFindingCondition) {
        try {
            return PageRequest.of(
                    generalFindingCondition.getPage(),
                    generalFindingCondition.getSize(),
                    switch (generalFindingCondition.getSortDirection()) {
                        case ASC -> Direction.ASC;
                        case DESC -> Direction.DESC;
                    },
                    generalFindingCondition.getSortBy().getName()
            );
        } catch (IllegalArgumentException e) {
            throw new EdonymyeonException(ExceptionInformation.POST_INVALID_PAGINATION_CONDITION);
        }
    }

    @Transactional
    public SpecificPostInfoResponse findSpecificPost(final Long postId, final MemberId memberId) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));

        final Optional<Member> member = memberRepository.findById(memberId.id());
        post.updateView(member.orElse(null));

        final ReactionCountResponse reactionCountResponse = new ReactionCountResponse(
                post.getViewCount(),
                0, // TODO: 댓글 수 기능 구현 필요
                0 // TODO: 스크랩 기능 구현 필요
        );
        final AllThumbsInPostResponse allThumbsInPost = thumbsService.findAllThumbsInPost(postId);
        final WriterDetailResponse writerDetailResponse = getWriterResponse(post.getMember());

        if (member.isEmpty()) {
            return SpecificPostInfoResponse.of(
                    post,
                    allThumbsInPost,
                    writerDetailResponse,
                    reactionCountResponse,
                    domain.getDomain()
            );
        }

        final ThumbsStatusInPostResponse thumbsStatusInPost = thumbsService.findThumbsStatusInPost(memberId, postId);

        return SpecificPostInfoResponse.of(
                post,
                allThumbsInPost,
                writerDetailResponse,
                reactionCountResponse,
                thumbsStatusInPost,
                member.get(),
                domain.getDomain()
        );
    }

    private WriterDetailResponse getWriterResponse(final Member member) {
        if (Objects.isNull(member.getProfileImageInfo())) {
            return new WriterDetailResponse(
                    member.getId(),
                    member.getNickname(),
                    null
            );
        }
        return new WriterDetailResponse(
                member.getId(),
                member.getNickname(),
                domain.getDomain() + member.getProfileImageInfo().getStoreName()
        );
    }

    public PostSlice<GeneralPostInfoResponse> searchPosts(final String searchWord,
                                                          final GeneralFindingCondition generalFindingCondition) {
        final Specification<Post> searchResults = PostSpecification.searchBy(searchWord);
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);

        Slice<GeneralPostInfoResponse> foundPosts = postRepository.findAll(searchResults, pageRequest)
                .map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()));
        return PostSlice.from(foundPosts);
    }

    public Slice<GeneralPostInfoResponse> findHotPosts(final HotFindingCondition hotFindingCondition) {
        String postIdsKey = HotPostPolicy.getPostIdsCacheKey(hotFindingCondition);
        String hasNextKey = HotPostPolicy.getHasNextCacheKey(hotFindingCondition);

        if (longCacheService.hasCache(postIdsKey)) {
            return findHotPostsFromCache(postIdsKey, hasNextKey);
        }
        return findHotPostFromRepository(hotFindingCondition, postIdsKey, hasNextKey);
    }

    private Slice<GeneralPostInfoResponse> findHotPostFromRepository(
            final HotFindingCondition hotFindingCondition,
            final String postIdsKey,
            final String hasNextKey
    ) {
        final Slice<Post> hotPost = postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.VIEW_COUNT_WEIGHT,
                HotPostPolicy.THUMBS_COUNT_WEIGHT,
                hotFindingCondition.toPage()
        );

        final List<Long> hotPostIds = hotPost.stream()
                .map(Post::getId)
                .toList();

        longCacheService.save(postIdsKey, hotPostIds);
        booleanCacheService.save(hasNextKey, hotPost.hasNext());

        return hotPost.map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()));
    }

    private Slice<GeneralPostInfoResponse> findHotPostsFromCache(String postIdsKey, String hasNextKey) {
        List<Long> postIds = longCacheService.getPostIds(postIdsKey);
        List<Post> posts = postRepository.findByIds(postIds);
        boolean hasNext = booleanCacheService.getHasNext(hasNextKey);
        return null;
    }
}
