package edonymyeon.backend.image.profileimage.repository;

import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfileImageInfoRepository extends JpaRepository<ProfileImageInfo, Long> {

    @Query(value = "select * from profile_image_info", nativeQuery = true)
    List<ProfileImageInfo> findAllImages();
}
