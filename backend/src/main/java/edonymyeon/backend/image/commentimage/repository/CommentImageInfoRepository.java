package edonymyeon.backend.image.commentimage.repository;

import edonymyeon.backend.image.commentimage.domain.CommentImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentImageInfoRepository extends JpaRepository<CommentImageInfo, Long> {

}
