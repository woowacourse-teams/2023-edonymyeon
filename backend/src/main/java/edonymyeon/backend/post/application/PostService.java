package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_FORBIDDEN;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.application.dto.ReactionCountResponse;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import edonymyeon.backend.post.application.dto.WriterDetailResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.post.repository.PostSpecification;
import edonymyeon.backend.thumbs.application.ThumbsService;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.dto.ThumbsStatusInPostResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;

    private final ImageFileUploader imageFileUploader;

    private final PostImageInfoRepository postImageInfoRepository;

    private final MemberRepository memberRepository;

    private final ThumbsService thumbsService;

    private final Domain domain;

    @Transactional
    public PostResponse createPost(final MemberIdDto memberIdDto, final PostRequest postRequest) {
        final Member member = findMemberById(memberIdDto);

        final Post post = new Post(
                postRequest.title(),
                postRequest.content(),
                postRequest.price(),
                member
        );
        postRepository.save(post);

        if (isImagesEmpty(postRequest)) {
            return new PostResponse(post.getId());
        }
        post.checkImageCount(postRequest.images().size());

        final PostImageInfos postImageInfos = PostImageInfos.of(post,
                imageFileUploader.uploadFiles(postRequest.images()));
        post.updateImages(postImageInfos);
        postImageInfoRepository.saveAll(postImageInfos.getPostImageInfos());

        return new PostResponse(post.getId());
    }

    private Member findMemberById(final MemberIdDto memberIdDto) {
        return memberRepository.findById(memberIdDto.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }

    private boolean isImagesEmpty(final PostRequest postRequest) {
        return Objects.isNull(postRequest.images()) ||
                postRequest.images().isEmpty() ||
                isDummy(postRequest.images().get(0)
                );
    }

    private boolean isDummy(final MultipartFile multipartFile) {
        return multipartFile.isEmpty();
    }

    @Transactional
    public void deletePost(final MemberIdDto memberIdDto, final Long postId) {
        final Member member = findMemberById(memberIdDto);
        final Post post = findPostById(postId);
        checkWriter(member, post);

        final List<PostImageInfo> postImageInfos = post.getPostImageInfos();
        thumbsService.deleteAllThumbsInPost(postId);
        postImageInfoRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
        postImageInfos.forEach(imageFileUploader::removeFile);
    }

    private Post findPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
    }

    private void checkWriter(final Member member, final Post post) {
        if (!post.isSameMember(member)) {
            throw new EdonymyeonException(POST_MEMBER_FORBIDDEN);
        }
    }

    @Transactional
    public PostResponse updatePost(
            final MemberIdDto memberId,
            final Long postId,
            final PostRequest postRequest
    ) {
        final Member member = findMemberById(memberId);
        final Post post = findPostById(postId);
        checkWriter(member, post);

        post.update(postRequest.title(), postRequest.content(), postRequest.price());

        final List<PostImageInfo> originalImageInfos = post.getPostImageInfos();
        postImageInfoRepository.deleteAllByPostId(postId);

        if (isImagesEmpty(postRequest)) {
            post.updateImages(PostImageInfos.create());
            originalImageInfos.forEach(imageFileUploader::removeFile);
            return new PostResponse(postId);
        }

        post.checkImageCount(postRequest.images().size());
        updateImagesOfPost(postRequest, post, originalImageInfos);
        return new PostResponse(postId);
    }

    private void updateImagesOfPost(
            final PostRequest postRequest,
            final Post post,
            final List<PostImageInfo> originalImageInfos
    ) {
        final PostImageInfos updatedPostImageInfos = PostImageInfos.of(post,
                imageFileUploader.uploadFiles(postRequest.images()));
        post.updateImages(updatedPostImageInfos);
        postImageInfoRepository.saveAll(updatedPostImageInfos.getPostImageInfos());
        originalImageInfos.forEach(imageFileUploader::removeFile);
    }

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
                        case ASC -> Direction.ASC;
                        case DESC -> Direction.DESC;
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

    public List<GeneralPostInfoResponse> searchPosts(final String searchWord, final GeneralFindingCondition generalFindingCondition) {
        final Specification<Post> searchResults = PostSpecification.searchBy(searchWord);
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);

        final Slice<Post> foundPosts = postRepository.findAll(searchResults, pageRequest);

        return foundPosts
                .stream()
                .map(post -> GeneralPostInfoResponse.of(post, domain.getDomain()))
                .toList();
    }
}
