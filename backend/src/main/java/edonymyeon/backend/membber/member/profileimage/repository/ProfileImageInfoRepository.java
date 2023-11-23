package edonymyeon.backend.membber.member.profileimage.repository;

import edonymyeon.backend.membber.member.profileimage.domain.ProfileImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageInfoRepository extends JpaRepository<ProfileImageInfo, Long> {

}
