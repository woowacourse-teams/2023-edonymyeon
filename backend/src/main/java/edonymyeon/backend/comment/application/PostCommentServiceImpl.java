package edonymyeon.backend.comment.application;

import edonymyeon.backend.comment.repository.CommentRepository;
import edonymyeon.backend.post.application.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PostCommentServiceImpl implements PostCommentService {

    private final CommentRepository commentRepository;

    @Override
    public int findCommentCountByPostId(final Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
