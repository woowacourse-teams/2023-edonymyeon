package edonymyeon.backend.member.profile.application;

import edonymyeon.backend.member.profile.domain.Device;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceToken(String deviceToken);

    @EntityGraph(attributePaths = "member")
    List<Device> findAllByDeviceToken(String deviceToken);

    Optional<Device> findByDeviceTokenAndIsActiveIsTrue(String deviceToken);
}
