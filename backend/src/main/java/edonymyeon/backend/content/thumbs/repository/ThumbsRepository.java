package edonymyeon.backend.content.thumbs.repository;

import edonymyeon.backend.content.thumbs.domain.Thumbs;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbsRepository extends JpaRepository<Thumbs, Long> {

    Optional<Thumbs> findByPostIdAndMemberId(final Long postId, final Long memberId);

    List<Thumbs> findByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);

    Long countByPostId(final Long postId);
}
