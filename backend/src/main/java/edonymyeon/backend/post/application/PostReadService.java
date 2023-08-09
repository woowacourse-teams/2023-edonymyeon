package edonymyeon.backend.post.application;

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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public Slice<GeneralPostInfoResponse> findPostsByPagingCondition(
            final GeneralFindingCondition generalFindingCondition) {
        PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        return postRepository.findAllBy(pageRequest)
                .map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()));
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

    public Slice<GeneralPostInfoResponse> searchPosts(final String searchWord,
                                                      final GeneralFindingCondition generalFindingCondition) {
        final Specification<Post> searchResults = PostSpecification.searchBy(searchWord);
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);

        final Slice<Post> foundPosts = postRepository.findAll(searchResults, pageRequest);
        return foundPosts.map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()));
    }

    public Slice<GeneralPostInfoResponse> findHotPosts(final HotFindingCondition hotFindingCondition) {
        final LocalDateTime findStartDate = LocalDateTime.now()
                .minus(HotPostPolicy.FINDING_PERIOD, ChronoUnit.DAYS);

        Slice<Post> hotPost = postRepository.findHotPosts(
                findStartDate,
                HotPostPolicy.VIEW_COUNT_WEIGHT,
                HotPostPolicy.THUMBS_COUNT_WEIGHT,
                hotFindingCondition.toPage()
        );

        return hotPost.map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()));
    }
}
