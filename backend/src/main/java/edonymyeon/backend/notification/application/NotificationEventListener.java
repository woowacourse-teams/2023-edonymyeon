package edonymyeon.backend.notification.application;

import edonymyeon.backend.comment.application.event.SavedCommentEvent;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.post.application.event.PostDeletionEvent;
import edonymyeon.backend.thumbs.application.event.ThumbsDownEvent;
import edonymyeon.backend.thumbs.application.event.ThumbsUpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

    @TransactionalEventListener
    public void sendCommentSavedNotification(SavedCommentEvent event) {
        try {
            notificationService.sendCommentNotificationToPostWriter(event.comment());
        } catch (BusinessLogicException e) {
            log.error(e.getMessage(), e);
        }
    }

    @TransactionalEventListener
    public void deleteNotificationFromDeletedPost(PostDeletionEvent event) {
        notificationService.deleteNotificationByPost(event.postId());
    }
}
