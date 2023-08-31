package edonymyeon.backend.notification.application.dto;

import edonymyeon.backend.notification.domain.Notification;
import java.util.List;

public record NotificationsResponse(List<NotificationResponse> notifications) {
    public static NotificationsResponse from(final List<Notification> notifications) {
        return new NotificationsResponse(
                notifications.stream()
                        .map(NotificationResponse::from)
                        .toList()
        );
    }

}
