package edonymyeon.backend.comment.application;

import edonymyeon.backend.comment.application.dto.request.CommentRequest;
import edonymyeon.backend.comment.application.dto.response.CommentDto;
import edonymyeon.backend.comment.application.dto.response.CommentsResponse;
import edonymyeon.backend.comment.application.event.SavedCommentEvent;
import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.comment.repository.CommentRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import edonymyeon.backend.image.commentimage.repository.CommentImageInfoRepository;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final CommentRepository commentRepository;

    private final CommentImageInfoRepository commentImageInfoRepository;

    private final ImageFileUploader imageFileUploader;

    private final ApplicationEventPublisher publisher;

    private final Domain domain;

    @Transactional
    public long createComment(final MemberId memberId, final Long postId, final CommentRequest commentRequest) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.POST_ID_NOT_FOUND));
        final Member member = memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.MEMBER_ID_NOT_FOUND));

        final Comment comment = new Comment(
                post,
                commentRequest.comment(),
                extractCommentImageInfo(commentRequest.image()),
                member
        );
        commentRepository.save(comment);

        publisher.publishEvent(new SavedCommentEvent(comment));

        return comment.getId();
    }

    private CommentImageInfo extractCommentImageInfo(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        final CommentImageInfo commentImageInfo = CommentImageInfo.from(imageFileUploader.uploadFile(image));
        commentImageInfoRepository.save(commentImageInfo);
        return commentImageInfo;
    }

    @Transactional
    public void deleteComment(final MemberId memberId, final Long postId, final Long commentId) {
        final Comment comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.COMMENT_ID_NOT_FOUND));
        comment.checkWriter(memberId.id());
        comment.delete();
    }

    public CommentsResponse findCommentsByPostId(final MemberId memberId, final Long postId) {
        if(!postRepository.existsById(postId)) {
            throw new EdonymyeonException(ExceptionInformation.POST_ID_NOT_FOUND);
        }

        final List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            final boolean isWriter = comment.isSameMember(memberId.id());
            final String imageUrl = convertToImageUrl(comment.getCommentImageInfo());
            final CommentDto commentDto = CommentDto.of(isWriter, comment, imageUrl);
            commentDtos.add(commentDto);
        }
        return new CommentsResponse(commentDtos.size(), commentDtos);
    }

    private String convertToImageUrl(final CommentImageInfo commentImageInfo) {
        if(commentImageInfo == null){
            return null;
        }
        final String imageFileName = commentImageInfo.getStoreName();
        return domain.convertToImageUrl(imageFileName);
    }
}
