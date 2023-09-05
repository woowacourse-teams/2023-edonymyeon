package edonymyeon.backend.post.application;

public interface PostCommentService {

    int findCommentCountByPostId(final Long postId);
}
