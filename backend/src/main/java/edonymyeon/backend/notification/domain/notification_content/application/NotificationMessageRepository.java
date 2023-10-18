package edonymyeon.backend.notification.domain.notification_content.application;

import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMessageRepository extends JpaRepository<NotificationContent, NotificationContentId> {

}
