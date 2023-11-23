package edonymyeon.backend.content.post.application;

public interface PostCommentService {

    void deleteAllCommentsInPost(final Long postId);
}
