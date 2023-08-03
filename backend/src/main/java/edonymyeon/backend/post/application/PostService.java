package edonymyeon.backend.post.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.domain.PostImageInfos;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.PostModificationRequest;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static edonymyeon.backend.global.exception.ExceptionInformation.*;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    private final ImageFileUploader imageFileUploader;

    private final PostImageInfoRepository postImageInfoRepository;

    private final MemberRepository memberRepository;

    private final PostThumbsService thumbsService;

    private final Domain domain;

    @Transactional
    public PostResponse createPost(final MemberId memberId, final PostRequest postRequest) {
        final Member member = findMemberById(memberId);

        final Post post = new Post(
                postRequest.title(),
                postRequest.content(),
                postRequest.price(),
                member
        );
        postRepository.save(post);

        if (isImagesEmpty(postRequest.newImages())) {
            return new PostResponse(post.getId());
        }
        post.validateImageAdditionCount(postRequest.newImages().size());

        final PostImageInfos postImageInfos = PostImageInfos.of(post,
                imageFileUploader.uploadFiles(postRequest.newImages()));
        post.updateImages(postImageInfos);
        postImageInfoRepository.saveAll(postImageInfos.getPostImageInfos());

        return new PostResponse(post.getId());
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
            throw new EdonymyeonException(POST_MEMBER_NOT_SAME);
        }
    }

    @Transactional
    public PostResponse updatePost(
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
        post.removePostImageInfos(deletedImagesOfPost);
        postImageInfoRepository.deleteAll(deletedImagesOfPost);

        if (isImagesEmpty(request.newImages())) {
            deletedImagesOfPost.forEach(imageFileUploader::removeFile);
            return new PostResponse(postId);
        }

        post.validateImageAdditionCount(request.newImages().size());
        updateImagesOfPost(request, post);
        deletedImagesOfPost.forEach(imageFileUploader::removeFile);
        return new PostResponse(postId);
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
