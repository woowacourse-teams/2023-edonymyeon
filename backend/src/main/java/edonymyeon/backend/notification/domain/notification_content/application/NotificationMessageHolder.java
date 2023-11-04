package edonymyeon.backend.notification.domain.notification_content.application;

import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 메시지의 캐싱 값을 관리하는 저장소입니다.
 */
@Slf4j
@Transactional(readOnly = true)
@Component
public class NotificationMessageHolder {

    private final NotificationMessageRepository notificationMessageRepository;

    public NotificationMessageHolder(final NotificationMessageRepository notificationMessageRepository) {
        this.notificationMessageRepository = notificationMessageRepository;
    }

    private final Map<NotificationContentId, NotificationContent> holder = new HashMap<>();

    @PostConstruct
    public void initialize() {
        for (NotificationContentId notificationContentId : NotificationContentId.values()) {
            final Optional<NotificationContent> notificationContent
                    = notificationMessageRepository.findById(notificationContentId);
            if (notificationContent.isEmpty()) {
                assignDefaultContent(notificationContentId);
            } else {
                holder.put(notificationContentId, notificationContent.get());
            }
        }
    }

    public NotificationContent findById(NotificationContentId notificationContentId) {
        final NotificationContent notificationContent = holder.get(notificationContentId);
        if (Objects.isNull(notificationContent)) {
            log.error("저장되거나 캐싱되지 않은 알림 메시지가 있음. : {}", notificationContentId.name());
            assignDefaultContent(notificationContentId);
            return notificationContentId.getDefaultContent();
        }
        return notificationContent;
    }

    private void assignDefaultContent(final NotificationContentId notificationContentId) {
        holder.put(notificationContentId, notificationContentId.getDefaultContent());
    }
}
