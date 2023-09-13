package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_NOT_SAME;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.Domain;
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

    private final PostCommentService commentService;

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
        post.validateImageCount(postRequest.newImages().size());

        final PostImageInfos postImageInfos = PostImageInfos.of(post,
                imageFileUploader.uploadFiles(postRequest.newImages()));
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

        // soft delete 시킬 때, 실제 이미지는 보관된다.
        // todo: 이미지 삭제.. 한번에..
        // todo: 소비내역 삭제할 때, 이벤트 대신 인터페이스로 변경
        applicationEventPublisher.publishEvent(new PostDeletionEvent(post.getId()));
        thumbsService.deleteAllThumbsInPost(postId);
        commentService.deleteAllCommentsInPost(postId);
        post.delete();
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

        final List<MultipartFile> imageFilesToAdd = request.newImages();
        final List<String> remainedImageNames = convertUrlToStoreName(request.originalImages());

        if(isImagesEmpty(imageFilesToAdd)) {
            post.updateImages(remainedImageNames);
            new PostIdResponse(postId);
        }

        final PostImageInfos imagesToAdd = PostImageInfos.of(post, imageFileUploader.uploadFiles(imageFilesToAdd));
        post.updateImages(remainedImageNames, imagesToAdd); //이때 기존 이미지중 삭제되는 것들은 softDelete
        postImageInfoRepository.saveAll(imagesToAdd.getPostImageInfos()); // //새로 추가된 이미지들을 DB에 저장
        return new PostIdResponse(postId);
    }

    private List<String> convertUrlToStoreName(final List<String> originalImageUrls) {
        if (originalImageUrls == null || originalImageUrls.isEmpty()) {
            return Collections.emptyList();
        }
        return domain.removeDomainFromUrl(originalImageUrls);
    }
}
