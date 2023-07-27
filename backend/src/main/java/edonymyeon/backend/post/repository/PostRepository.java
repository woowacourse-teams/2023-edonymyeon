package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "member")
    Slice<Post> findAllBy(PageRequest pageRequest);
}
