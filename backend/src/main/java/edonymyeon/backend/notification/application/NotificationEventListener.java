package edonymyeon.backend.notification.application;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.thumbs.application.event.ThumbsDownEvent;
import edonymyeon.backend.thumbs.application.event.ThumbsUpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener
    public void sendThumbsUpNotification(ThumbsUpEvent event) {
        try {
            notificationService.sendThumbsNotificationToWriter(event.post());
        } catch (BusinessLogicException e) {
            log.error(e.getMessage(), e);
        }
    }

    @TransactionalEventListener
    public void sendThumbsDownNotification(ThumbsDownEvent event) {
        try {
            notificationService.sendThumbsNotificationToWriter(event.post());
        } catch (BusinessLogicException e) {
            log.error(e.getMessage(), e);
        }
    }
}
