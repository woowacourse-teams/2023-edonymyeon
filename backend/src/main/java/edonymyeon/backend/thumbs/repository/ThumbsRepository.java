package edonymyeon.backend.thumbs.repository;

import edonymyeon.backend.thumbs.domain.Thumbs;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbsRepository extends JpaRepository<Thumbs, Long> {

    Optional<Thumbs> findByPostIdAndMemberId(Long postId, Long memberId);
}
