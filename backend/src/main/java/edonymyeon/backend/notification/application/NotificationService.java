package edonymyeon.backend.notification.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.NOTIFICATION_REQUEST_FAILED;
import static edonymyeon.backend.notification.domain.NotificationMessage.THUMBS_NOTIFICATION_TITLE;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationSender notificationSender;

    private final NotificationRepository notificationRepository;

    public void sendThumbsNotificationToWriter(final Post post) {
        final Receiver receiver = new Receiver(post.getMember());
        final boolean isSentSuccessfully = notificationSender.sendNotification(
                receiver,
                THUMBS_NOTIFICATION_TITLE.getMessage()
        );

        if (!isSentSuccessfully) {
            throw new BusinessLogicException(NOTIFICATION_REQUEST_FAILED);
        }

        final Notification notification = new Notification(THUMBS_NOTIFICATION_TITLE.getMessage(), ScreenType.POST,
                post.getId());
        notificationRepository.save(notification);
    }
}
