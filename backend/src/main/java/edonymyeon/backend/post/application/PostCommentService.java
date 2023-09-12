package edonymyeon.backend.post.application;

public interface PostCommentService {

    void deleteAllCommentsInPost(final Long postId);
}
