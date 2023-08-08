package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @EntityGraph(attributePaths = "member")
    Slice<Post> findAllBy(PageRequest pageRequest);

    @NonNull
    @EntityGraph(attributePaths = "member")
    Page<Post> findAll(@NonNull Specification<Post> specification,
                       @NonNull Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Slice<Post> findAllByMemberId(Long memberId, Pageable pageable);
}
