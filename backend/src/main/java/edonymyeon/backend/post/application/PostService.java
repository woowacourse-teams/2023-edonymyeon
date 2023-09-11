package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_NOT_SAME;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.request.PostModificationRequest;
import edonymyeon.backend.post.application.dto.request.PostRequest;
import edonymyeon.backend.post.application.dto.response.PostIdResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.post.application.event.PostDeletionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    private final ImageFileUploader imageFileUploader;

    private final PostImageInfoRepository postImageInfoRepository;

    private final MemberRepository memberRepository;

    private final PostThumbsService thumbsService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Domain domain;

    @Transactional
    public PostIdResponse createPost(final MemberId memberId, final PostRequest postRequest) {
        final Member member = findMemberById(memberId);

        final Post post = new Post(
                postRequest.title(),
                postRequest.content(),
                postRequest.price(),
                member
        );
        postRepository.save(post);

        if (isImagesEmpty(postRequest.newImages())) {
            return new PostIdResponse(post.getId());
        }
        post.validateImageAdditionCount(postRequest.newImages().size());

        final PostImageInfos postImageInfos = PostImageInfos.of(post,
                imageFileUploader.uploadFiles(postRequest.newImages()));
        post.updateImages(postImageInfos);
        postImageInfoRepository.saveAll(postImageInfos.getPostImageInfos());

        return new PostIdResponse(post.getId());
    }

    private Member findMemberById(final MemberId memberId) {
        return memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }

    private boolean isImagesEmpty(final List<MultipartFile> images) {
        return Objects.isNull(images) ||
                images.isEmpty() ||
                isDummy(images.get(0)
                );
    }

    private boolean isDummy(final MultipartFile multipartFile) {
        return multipartFile.isEmpty();
    }

    @Transactional
    public void deletePost(final MemberId memberId, final Long postId) {
        final Member member = findMemberById(memberId);
        final Post post = findPostById(postId);
        checkWriter(member, post);

        final List<PostImageInfo> postImageInfos = post.getPostImageInfos();
        final ArrayList<PostImageInfo> copyOfPostImageInfos = new ArrayList<>(postImageInfos);
        // todo: 소비내역 삭제할 때, 이벤트 대신 인터페이스로 변경
        applicationEventPublisher.publishEvent(new PostDeletionEvent(post.getId()));
        thumbsService.deleteAllThumbsInPost(postId);
        // todo: 게시글을 soft delete로 변경하면서 댓글, 게시글 이미지를 soft delete 하는 코드 작성
        // todo: soft delete로 변경 후신고된 게시글 삭제 이슈해결되는지 확인
        // todo: 추천/비추천과 소비내역은 물리적 삭제로 진행
        postImageInfoRepository.deleteAllByPostId(postId);
        postRepository.deleteById(postId);
        copyOfPostImageInfos.forEach(imageFileUploader::removeFile);
    }

    private Post findPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
    }

    private void checkWriter(final Member member, final Post post) {
        if (!post.isSameMember(member)) {
            throw new EdonymyeonException(POST_MEMBER_NOT_SAME);
        }
    }

    @Transactional
    public PostIdResponse updatePost(
            final MemberId memberId,
            final Long postId,
            final PostModificationRequest request
    ) {
        final Member member = findMemberById(memberId);
        final Post post = findPostById(postId);
        checkWriter(member, post);

        post.update(request.title(), request.content(), request.price());

        final List<String> imageStoreNames = convertUrlToStoreName(request.originalImages());
        final List<PostImageInfo> deletedImagesOfPost = post.findImagesToDelete(imageStoreNames);
        final ArrayList<PostImageInfo> copyOfDeletedImagesOfPost = new ArrayList<>(deletedImagesOfPost);

        post.removePostImageInfos(deletedImagesOfPost);
        postImageInfoRepository.deleteAll(deletedImagesOfPost);

        if (isImagesEmpty(request.newImages())) {
            copyOfDeletedImagesOfPost.forEach(imageFileUploader::removeFile);
            return new PostIdResponse(postId);
        }

        post.validateImageAdditionCount(request.newImages().size());
        updateImagesOfPost(request, post);
        deletedImagesOfPost.forEach(imageFileUploader::removeFile);
        return new PostIdResponse(postId);
    }

    private List<String> convertUrlToStoreName(final List<String> originalImageUrls) {
        if (originalImageUrls == null || originalImageUrls.isEmpty()) {
            return Collections.emptyList();
        }
        return domain.removeDomainFromUrl(originalImageUrls);
    }

    private void updateImagesOfPost(final PostModificationRequest request, final Post post) {
        final PostImageInfos updatedPostImageInfos = PostImageInfos.of(post,
                imageFileUploader.uploadFiles(request.newImages()));
        post.updateImages(updatedPostImageInfos);
        postImageInfoRepository.saveAll(updatedPostImageInfos.getPostImageInfos());
    }
}
