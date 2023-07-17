package edonymyeon.backend.repository;

import edonymyeon.backend.domain.PostImageInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageInfoRepository extends JpaRepository<PostImageInfo, Long> {

    List<PostImageInfo> findAllByPostId(Long postId);
}
