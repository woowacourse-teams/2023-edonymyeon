package edonymyeon.backend.content.comment.application;

import edonymyeon.backend.content.comment.domain.Comment;
import edonymyeon.backend.content.comment.repository.CommentRepository;
import edonymyeon.backend.content.comment.commentimage.repository.CommentImageInfoRepository;
import edonymyeon.backend.content.post.application.PostCommentService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final CommentRepository commentRepository;
    private final CommentImageInfoRepository commentImageInfoRepository;

    @Override
    @Transactional
    public void deleteAllCommentsInPost(final Long postId) {
        final List<Comment> comments = commentRepository.findAllByPostId(postId);
        final List<Long> imageIds = new ArrayList<>();
        for (Comment comment : comments) {
            if(comment.hasImage()){
                imageIds.add(comment.getCommentImageInfo().getId());
            }
        }
        commentImageInfoRepository.deleteAllById(imageIds);
        commentRepository.deleteAllByPostId(postId);
    }
}
