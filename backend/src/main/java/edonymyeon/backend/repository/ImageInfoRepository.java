package edonymyeon.backend.repository;

import edonymyeon.backend.domain.ImageInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageInfoRepository extends JpaRepository<ImageInfo, Long> {

    List<ImageInfo> findAllByPostId(Long postId);
}
