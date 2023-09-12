package edonymyeon.backend.comment.repository;

import edonymyeon.backend.comment.domain.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"member", "commentImageInfo"})
    Optional<Comment> findByIdAndPostId(final Long id, final Long postId);

    @EntityGraph(attributePaths = {"post", "member", "commentImageInfo"})
    List<Comment> findAllByPostId(final Long postId);

    @Modifying(flushAutomatically = true, clearAutomatically = false)
    @Query("update Comment c set c.deleted = true where c.post.id = :postId")
    void deleteAllByPostId(@Param("postId") Long postId);
}
