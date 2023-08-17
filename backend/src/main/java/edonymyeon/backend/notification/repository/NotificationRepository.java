package edonymyeon.backend.notification.repository;

import edonymyeon.backend.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByMemberId(Long memberId);
}
