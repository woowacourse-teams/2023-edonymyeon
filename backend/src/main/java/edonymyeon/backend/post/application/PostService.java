package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_FORBIDDEN;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.postimage.PostImageInfoRepository;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
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

        if (Objects.isNull(postRequest.images()) || postRequest.images().isEmpty() || isDummy(
                postRequest.images().get(0))) {
            return new PostResponse(post.getId());
        }

        final List<PostImageInfo> postImageInfos = uploadImages(postRequest).stream()
                .map(imageInfo -> PostImageInfo.of(imageInfo, post))
                .toList();
        postImageInfoRepository.saveAll(postImageInfos);

        return new PostResponse(post.getId());
    }

    private Member findMemberById(final MemberIdDto memberIdDto) {
        return memberRepository.findById(memberIdDto.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }

    private boolean isDummy(final MultipartFile multipartFile) {
        return multipartFile.isEmpty();
    }

    private List<ImageInfo> uploadImages(final PostRequest postRequest) {
        return postRequest.images()
                .stream()
                .map(imageFileUploader::uploadFile)
                .toList();
    }

    @Transactional
    public void deletePost(final MemberIdDto memberIdDto, final Long postId) {
        final Member member = findMemberById(memberIdDto);
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
        if (post.isSameMember(member)) {
            final List<ImageInfo> imageInfos = post.getPostImageInfos()
                    .stream()
                    .map(postImage -> new ImageInfo(postImage.getFileDirectory(), postImage.getStoreName()))
                    .toList();
            postImageInfoRepository.deleteAllByPostId(postId);
            postRepository.deleteById(postId);
            imageInfos.forEach(imageFileUploader::removeFile);
            return;
        }
        throw new EdonymyeonException(POST_MEMBER_FORBIDDEN);
    }

    public List<GeneralPostInfoResponse> findAllPost(final GeneralFindingCondition generalFindingCondition) {
        final PageRequest pageRequest = convertConditionToPageRequest(generalFindingCondition);
        final Slice<Post> foundPosts = postRepository.findAll(pageRequest);
        return foundPosts
                .map(GeneralPostInfoResponse::from)
                .toList();
    }

    private static PageRequest convertConditionToPageRequest(final GeneralFindingCondition generalFindingCondition) {
        return PageRequest.of(
                generalFindingCondition.getPage(),
                generalFindingCondition.getSize(),
                switch (generalFindingCondition.getSortDirection()) {
                    case ASC -> Direction.ASC;
                    case DESC -> Direction.DESC;
                },
                generalFindingCondition.getSortBy().getName()
        );
    }
}
