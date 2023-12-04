package edonymyeon.backend.notification.domain.notification_content.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 알림 메시지의 캐싱 값을 관리하는 저장소입니다.
 */
@Slf4j
@Transactional
@Component
public class NotificationContentHolder {

    private final NotificationContentRepository notificationContentRepository;

    public NotificationContentHolder(final NotificationContentRepository notificationContentRepository) {
        this.notificationContentRepository = notificationContentRepository;
    }

    private final Map<NotificationContentId, NotificationContent> holder = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialize() {
        for (NotificationContentId notificationContentId : NotificationContentId.values()) {
            final Optional<NotificationContent> notificationContent
                    = notificationContentRepository.findById(notificationContentId);
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
            log.warn("{}에 해당하는 알림 메시지가 캐싱되지 않았음. DB에서 조회하는 중", notificationContentId);
            final Optional<NotificationContent> content = notificationContentRepository.findById(notificationContentId);

            if (content.isPresent()) {
                holder.put(notificationContentId, content.get());
                return content.get();
            }

            log.error("{}에 해당하는 알림 메시지가 DB에 저장되지도 캐싱되지도 않았음. 기본 메시지를 발송", notificationContentId);
            assignDefaultContent(notificationContentId);
            return notificationContentId.getDefaultContent();
        }
        return notificationContent;
    }

    private void assignDefaultContent(final NotificationContentId notificationContentId) {
        holder.put(notificationContentId, notificationContentId.getDefaultContent());
    }

    @Transactional
    public void updateMessage(final NotificationContent contentToUpdate) {
        final Optional<NotificationContent> notificationContent
                = notificationContentRepository.findById(contentToUpdate.getId());

        notificationContent.ifPresentOrElse(content -> content.update(contentToUpdate), () -> {
            throw new EdonymyeonException(
                    ExceptionInformation.NOTIFICATION_MESSAGE_NOT_FOUND);
        });

        holder.put(contentToUpdate.getId(), contentToUpdate);
    }
}
