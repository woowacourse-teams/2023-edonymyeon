package edonymyeon.backend.image.postimage;

import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageInfoRepository extends JpaRepository<PostImageInfo, Long> {

    List<PostImageInfo> findAllByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);
}
