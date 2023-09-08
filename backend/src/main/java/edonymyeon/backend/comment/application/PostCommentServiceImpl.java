package edonymyeon.backend.comment.application;

import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.comment.repository.CommentRepository;
import edonymyeon.backend.post.application.PostCommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostCommentServiceImpl implements PostCommentService {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void deleteAllCommentsInPost(final Long postId) {
        final List<Comment> comments = commentRepository.findAllByPostId(postId);
        comments.forEach(Comment::delete);
    }
}
