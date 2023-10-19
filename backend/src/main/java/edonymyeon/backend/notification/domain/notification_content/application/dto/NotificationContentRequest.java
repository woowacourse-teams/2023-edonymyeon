package edonymyeon.backend.notification.domain.notification_content.application.dto;

import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;

public record NotificationContentRequest(NotificationContentId id, String title, String body) {
}
