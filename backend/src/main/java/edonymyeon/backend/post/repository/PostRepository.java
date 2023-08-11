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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @EntityGraph(attributePaths = "member")
    Slice<Post> findAllBy(PageRequest pageRequest);

    @NonNull
    @EntityGraph(attributePaths = "member")
    Page<Post> findAll(@NonNull Specification<Post> specification,
                       @NonNull Pageable pageable);

    @EntityGraph(attributePaths = "member")
    Slice<Post> findAllByMemberId(Long memberId, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN Thumbs t
            ON p.id = t.post.id
            WHERE p.createdAt >= :findStartDate
            GROUP BY p.id
            ORDER BY (COUNT(t.id) * :thumbsCountWeight + p.viewCount * :viewCountWeight) DESC, p.createdAt ASC
            """)
    Slice<Post> findHotPosts(
            @Param("findStartDate") final LocalDateTime findStartDate,
            @Param("viewCountWeight") final int viewCountWeight,
            @Param("thumbsCountWeight") final int thumbsCountWeight,
            Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("SELECT p FROM Post p WHERE p.id IN (:postIds)")
    List<Post> findByIds(@Param("postIds") final List<Long> postIds);
}
