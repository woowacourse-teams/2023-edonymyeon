package edonymyeon.backend.repository;

import edonymyeon.backend.domain.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

}
