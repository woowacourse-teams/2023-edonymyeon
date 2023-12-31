package edonymyeon.backend.notification.application.dto;

import edonymyeon.backend.notification.domain.Notification;

public record NotificationResponse(
        Long id,
        String title,
        String navigateTo,
        Long postId,
        boolean read) {

    public static NotificationResponse from(final Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getScreenType().name(),
                notification.getPostId(),
                notification.isRead()
        );
    }
}
