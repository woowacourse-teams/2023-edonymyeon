package edonymyeon.backend.thumbs.repository;

import edonymyeon.backend.thumbs.domain.Thumbs;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbsRepository extends JpaRepository<Thumbs, Long> {

    Optional<Thumbs> findByPostIdAndMemberId(final Long postId, final Long memberId);

    List<Thumbs> findByPostId(Long postId);
}
