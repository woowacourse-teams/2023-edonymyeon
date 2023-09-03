package edonymyeon.backend.comment.repository;

import edonymyeon.backend.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"member", "commentImageInfo"})
    Optional<Comment> findByIdAndPostId(final Long id, final Long postId);
}
