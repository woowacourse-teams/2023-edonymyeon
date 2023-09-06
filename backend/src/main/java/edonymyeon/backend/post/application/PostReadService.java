package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static org.springframework.data.domain.Sort.Direction;

import edonymyeon.backend.cache.application.PostCachingService;
import edonymyeon.backend.cache.application.dto.CachedPostResponse;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.response.AllThumbsInPostResponse;
import edonymyeon.backend.post.application.dto.response.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.response.ReactionCountResponse;
import edonymyeon.backend.post.application.dto.response.SpecificPostInfoResponse;
import edonymyeon.backend.post.application.dto.response.ThumbsStatusInPostResponse;
import edonymyeon.backend.post.application.dto.response.WriterDetailResponse;
import edonymyeon.backend.post.domain.HotPostPolicy;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.post.repository.PostSpecification;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReadService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final PostThumbsService thumbsService;

    private final Domain domain;

    private final PostCachingService postCachingService;

    /**
     * 게시글 전체 목록 조회
     */
    public PostSlice<GeneralPostInfoResponse> findPostsByPagingCondition(
            final GeneralFindingCondition generalFindingCondition) {
        PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        Slice<GeneralPostInfoResponse> posts = postRepository.findAllBy(pageRequest)
                .map(post -> GeneralPostInfoResponse.of(
                        post,
                        domain.getDomain()
                ));
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

    /**
     * 게시글 검색
     */
    public PostSlice<GeneralPostInfoResponse> searchPosts(final String searchWord,
                                                          final GeneralFindingCondition generalFindingCondition) {
        final Specification<Post> searchResults = PostSpecification.searchBy(searchWord);
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);

        Slice<GeneralPostInfoResponse> foundPosts = postRepository.findAll(searchResults, pageRequest)
                .map(post -> GeneralPostInfoResponse.of(
                        post,
                        domain.getDomain()
                ));
        return PostSlice.from(foundPosts);
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
        final Slice<Post> hotPost = findHotPostSliceFromRepositoryByPolicy(hotFindingCondition);
        postCachingService.cachePosts(hotFindingCondition, hotPost);
        Slice<GeneralPostInfoResponse> hotPostSlice = hotPost.map(post -> GeneralPostInfoResponse.of(
                        post,
                        domain.getDomain()
                )
        );
        return PostSlice.from(hotPostSlice);
    }

    private PostSlice<GeneralPostInfoResponse> findCachedPosts(final HotFindingCondition hotFindingCondition) {
        CachedPostResponse cachedHotPosts = postCachingService.findCachedPosts(hotFindingCondition);
        List<Post> hotPostsInRepository = postRepository.findByIds(cachedHotPosts.postIds());

        if (cachedHotPosts.size() != hotPostsInRepository.size()) {
            return findHotPostFromRepository(hotFindingCondition);
        }

        List<GeneralPostInfoResponse> posts = postRepository.findByIds(cachedHotPosts.postIds())
                .stream()
                .map(post -> GeneralPostInfoResponse.of(
                        post,
                        domain.getDomain()
                ))
                .toList();
        return new PostSlice<>(posts, cachedHotPosts.isLast());
    }

    private Slice<Post> findHotPostSliceFromRepositoryByPolicy(final HotFindingCondition hotFindingCondition) {
        return postRepository.findHotPosts(
                HotPostPolicy.getFindPeriod(),
                HotPostPolicy.getViewCountWeight(),
                HotPostPolicy.getThumbsCountWeight(),
                hotFindingCondition.toPage()
        );
    }
}
