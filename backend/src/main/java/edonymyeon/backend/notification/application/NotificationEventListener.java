package edonymyeon.backend.notification.application;

import edonymyeon.backend.thumbs.application.event.ThumbsUpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void listen(ThumbsUpEvent event) {
        notificationService.sendThumbsNotificationToWriter(event.post());
    }
}
