package edonymyeon.backend.post.repository;

import edonymyeon.backend.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    // todo: 매번 devices까지 가져오는 것도 구린 듯. 리팩터링 필요
    @Override
    @EntityGraph(attributePaths = {"member", "member.devices"})
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = "member")
    Slice<Post> findAllBy(PageRequest pageRequest);

    @Override
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
            LEFT JOIN Comment c
            ON p.id = c.post.id
            WHERE p.createdAt >= :findStartDate
            GROUP BY p.id
            ORDER BY (COUNT(t.id) * :thumbsCountWeight + COUNT(c.id) * :commentCountWeight + p.viewCount * :viewCountWeight) DESC
            """)
    Slice<Post> findHotPosts(
            @Param("findStartDate") final LocalDateTime findStartDate,
            @Param("viewCountWeight") final int viewCountWeight,
            @Param("thumbsCountWeight") final int thumbsCountWeight,
            @Param("commentCountWeight") final int commentCountWeight,
            final Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("SELECT p FROM Post p WHERE p.id IN (:postIds) ORDER BY p.id DESC")
    List<Post> findByIds(@Param("postIds") final List<Long> postIds);
}
