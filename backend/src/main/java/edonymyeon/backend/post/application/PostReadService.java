package edonymyeon.backend.post.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.*;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.post.repository.PostSpecification;
import edonymyeon.backend.thumbs.application.ThumbsService;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.dto.ThumbsStatusInPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostReadService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final ThumbsService thumbsService;

    private final Domain domain;

    public List<GeneralPostInfoResponse> findPostsByPagingCondition(final GeneralFindingCondition generalFindingCondition) {
        PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        final Slice<Post> foundPosts = postRepository.findAllBy(pageRequest);
        return foundPosts
                .map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()))
                .toList();
    }

    private static PageRequest convertConditionToPageRequest(final GeneralFindingCondition generalFindingCondition) {
        try {
            return PageRequest.of(
                    generalFindingCondition.getPage(),
                    generalFindingCondition.getSize(),
                    switch (generalFindingCondition.getSortDirection()) {
                        case ASC -> Sort.Direction.ASC;
                        case DESC -> Sort.Direction.DESC;
                    },
                    generalFindingCondition.getSortBy().getName()
            );
        } catch (IllegalArgumentException e) {
            throw new EdonymyeonException(ExceptionInformation.POST_INVALID_PAGINATION_CONDITION);
        }
    }

    public SpecificPostInfoResponse findSpecificPost(final Long postId, final MemberIdDto memberIdDto) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));

        final Optional<Member> member = memberRepository.findById(memberIdDto.id());

        final ReactionCountResponse reactionCountResponse = new ReactionCountResponse(
                0, // TODO: 조회수 기능 구현 필요
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

        final ThumbsStatusInPostResponse thumbsStatusInPost = thumbsService.findThumbsStatusInPost(memberIdDto, postId);

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

    public GeneralPostsResponse searchPosts(final String searchWord, final GeneralFindingCondition generalFindingCondition) {
        final Specification<Post> searchResults = PostSpecification.searchBy(searchWord);
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);

        final Slice<Post> foundPosts = postRepository.findAll(searchResults, pageRequest);

        List<GeneralPostInfoResponse> posts = foundPosts.stream()
                .map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()))
                .toList();

        return new GeneralPostsResponse(posts);
    }
}
