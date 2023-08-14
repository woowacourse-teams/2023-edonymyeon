package edonymyeon.backend.notification.application.dto;

import edonymyeon.backend.notification.domain.Notification;
import java.util.List;
import java.util.stream.Collectors;

public record NotificationsResponse(List<NotificationResponse> notifications) {
    public static NotificationsResponse from(final List<Notification> notifications) {
        return new NotificationsResponse(
                notifications.stream().map(NotificationResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
