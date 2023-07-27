package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p "
            + "WHERE p.title like %:query% or p.content like %:query% "
            + "ORDER BY p.createdAt desc")
    List<Post> searchBy(@Param("query") final String query);
}
