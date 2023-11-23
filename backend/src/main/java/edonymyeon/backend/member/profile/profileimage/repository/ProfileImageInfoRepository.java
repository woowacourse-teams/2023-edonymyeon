package edonymyeon.backend.member.profile.profileimage.repository;

import edonymyeon.backend.member.profile.profileimage.domain.ProfileImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageInfoRepository extends JpaRepository<ProfileImageInfo, Long> {

}
