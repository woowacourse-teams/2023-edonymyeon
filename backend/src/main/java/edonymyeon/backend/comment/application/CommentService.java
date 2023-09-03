package edonymyeon.backend.comment.application;

import edonymyeon.backend.comment.application.dto.CommentRequest;
import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.comment.repository.CommentRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import edonymyeon.backend.image.commentimage.repository.CommentImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
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
        return comment.getId();
    }

    private CommentImageInfo extractCommentImageInfo(final MultipartFile image) {
        if (image.isEmpty()) {
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
}
