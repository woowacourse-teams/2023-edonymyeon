package edonymyeon.backend.notification.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.NOTIFICATION_REQUEST_FAILED;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    public static final String THUMBS_NOTIFICATION_TITLE = "당신의 글에 누군가 반응을 남겼습니다.";

    public static final String THUMBS_NOTIFICATION_CONTENT = "클릭하여 확인해보세요!";

    private final NotificationSender notificationSender;

    public void sendThumbsNotificationToWriter(final Post post) {
        final Receiver receiver = new Receiver(post.getMember());
        final boolean isSentSuccessfully = notificationSender.sendNotification(
                receiver,
                THUMBS_NOTIFICATION_TITLE,
                THUMBS_NOTIFICATION_CONTENT
        );

        if (!isSentSuccessfully) {
            throw new BusinessLogicException(NOTIFICATION_REQUEST_FAILED);
        }
    }
}
