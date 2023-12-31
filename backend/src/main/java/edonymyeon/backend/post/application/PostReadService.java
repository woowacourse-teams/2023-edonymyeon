package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static org.springframework.data.domain.Sort.Direction;

import edonymyeon.backend.cache.application.PostCachingService;
import edonymyeon.backend.cache.application.dto.CachedPostResponse;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.application.ImageService;
import edonymyeon.backend.image.domain.ImageType;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.response.*;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReadService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final PostThumbsService thumbsService;

    private final ImageService imageService;

    private final PostCachingService postCachingService;

    /**
     * 게시글 전체 목록 조회
     */
    public PostSlice<GeneralPostInfoResponse> findPostsByPagingCondition(
            final GeneralFindingCondition generalFindingCondition) {
        PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        Slice<Post> posts = postRepository.findAllBy(pageRequest);
        return PostSlice.from(GeneralPostsInfoResponse.toSlice(posts, imageService.findBaseUrl(ImageType.POST)));
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

    /**
     * 게시글 상세 조회
     */
    @Transactional
    public SpecificPostInfoResponse findSpecificPost(final Long postId, final MemberId memberId) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));

        final Optional<Member> member = memberRepository.findById(memberId.id());
        post.updateView(member.orElse(null));

        final ReactionCountResponse reactionCountResponse = new ReactionCountResponse(
                post.getViewCount(),
                post.getCommentCount()
        );
        final AllThumbsInPostResponse allThumbsInPost = thumbsService.findAllThumbsInPost(postId);
        final WriterDetailResponse writerDetailResponse = getWriterResponse(post.getMember());

        if (member.isEmpty()) {
            return SpecificPostInfoResponse.of(
                    post,
                    allThumbsInPost,
                    writerDetailResponse,
                    reactionCountResponse,
                    imageService.findBaseUrl(ImageType.POST)
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
                imageService.findBaseUrl(ImageType.POST)
        );
    }

    private WriterDetailResponse getWriterResponse(final Member member) {
        return new WriterDetailResponse(
                member.getId(),
                member.getNickname(),
                imageService.convertToImageUrl(member.getProfileImageInfo(), ImageType.PROFILE)
        );
    }

    /**
     * 게시글 검색
     */
    public PostSlice<GeneralPostInfoResponse> searchPosts(final String searchWord,
                                                          final GeneralFindingCondition generalFindingCondition) {
        final Specification<Post> searchResults = PostSpecification.searchBy(searchWord);
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        Slice<Post> foundPosts = postRepository.findAll(searchResults, pageRequest);
        return PostSlice.from(GeneralPostsInfoResponse.toSlice(foundPosts, imageService.findBaseUrl(ImageType.POST)));
    }

    /**
     * 핫 게시글 조회
     */
    public PostSlice<GeneralPostInfoResponse> findHotPosts(final HotFindingCondition hotFindingCondition) {
        if (postCachingService.isNotCached(hotFindingCondition)) {
            return findHotPostFromRepository(hotFindingCondition);
        }
        if (postCachingService.shouldRefreshCache(hotFindingCondition)) {
            return findHotPostFromRepository(hotFindingCondition);
        }
        return findCachedPosts(hotFindingCondition);
    }

    private PostSlice<GeneralPostInfoResponse> findHotPostFromRepository(
            final HotFindingCondition hotFindingCondition) {
        final Slice<Post> hotPosts = findHotPostSliceFromRepositoryByPolicy(hotFindingCondition);
        postCachingService.cachePosts(hotFindingCondition, hotPosts);

        List<Post> sortedHotPosts = hotPosts.stream()
                .sorted(Comparator.comparing(Post::getId).reversed())
                .toList();
        return new PostSlice<>(GeneralPostsInfoResponse.toList(sortedHotPosts, imageService.findBaseUrl(ImageType.POST)), hotPosts.isLast());
    }

    private PostSlice<GeneralPostInfoResponse> findCachedPosts(final HotFindingCondition hotFindingCondition) {
        CachedPostResponse cachedHotPosts = postCachingService.findCachedPosts(hotFindingCondition);
        List<Post> hotPostsInRepository = postRepository.findByIds(cachedHotPosts.postIds());
        if (cachedHotPosts.size() != hotPostsInRepository.size()) {
            return findHotPostFromRepository(hotFindingCondition);
        }
        return new PostSlice<>(GeneralPostsInfoResponse.toList(hotPostsInRepository, imageService.findBaseUrl(ImageType.POST)), cachedHotPosts.isLast());
    }

    private Slice<Post> findHotPostSliceFromRepositoryByPolicy(final HotFindingCondition hotFindingCondition) {
        return postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.getViewCountWeight(),
                HotPostPolicy.getThumbsCountWeight(),
                HotPostPolicy.getCommentCountWeight(),
                hotFindingCondition.toPage()
        );
    }
}
