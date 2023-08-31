package edonymyeon.backend.notification.domain;

public enum NotificationMessage {

    THUMBS_NOTIFICATION_TITLE("당신의 글에 누군가 반응을 남겼습니다.");

    private final String message;

    NotificationMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
