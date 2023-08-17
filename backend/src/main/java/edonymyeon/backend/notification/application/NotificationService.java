package edonymyeon.backend.notification.application;

import static edonymyeon.backend.notification.domain.NotificationMessage.THUMBS_NOTIFICATION_TITLE;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.notification.application.dto.NotificationsResponse;
import edonymyeon.backend.notification.domain.Data;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.domain.Post;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationSender notificationSender;

    private final NotificationRepository notificationRepository;

    public NotificationsResponse findNotifications(MemberId memberId) {
        final List<Notification> notifications = notificationRepository.findByMemberId(memberId.id());
        notifications.sort(Comparator.comparing((Notification o) -> o.getCreatedAt()).reversed());
        return NotificationsResponse.from(notifications);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendThumbsNotificationToWriter(final Post post) {
        final Optional<String> deviceToken = post.getMember().getActiveDeviceToken();

        if (deviceToken.isEmpty()) {
            return;
        }

        final Receiver receiver = new Receiver(post.getMember(), new Data(ScreenType.POST, post.getId()));
        try {
            notificationSender.sendNotification(
                    receiver,
                    THUMBS_NOTIFICATION_TITLE.getMessage()
            );
        } catch (BusinessLogicException e) {
            log.error("알림 전송에 실패했습니다.", e);
        }

        saveNotification(post);
    }

    private void saveNotification(final Post post) {
        final Notification notification = new Notification(
                post.getMember(),
                THUMBS_NOTIFICATION_TITLE.getMessage(),
                ScreenType.POST,
                post.getId()
        );
        notificationRepository.save(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markNotificationAsRead(final String notificationId) {
        final Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(Notification::markAsRead);
    }
}
