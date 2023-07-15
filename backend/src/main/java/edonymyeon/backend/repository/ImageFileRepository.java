package edonymyeon.backend.repository;

import edonymyeon.backend.domain.ImageFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findAllByPostId(Long postId);
}
