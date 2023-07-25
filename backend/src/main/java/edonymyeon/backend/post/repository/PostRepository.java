package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    Slice<Post> findAllBy(PageRequest pageRequest);
}
