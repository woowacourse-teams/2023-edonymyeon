package edonymyeon.backend.image.profileimage.repository;

import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageInfoRepository extends JpaRepository<ProfileImageInfo, Long> {

}
