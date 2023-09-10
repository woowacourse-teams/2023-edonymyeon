package edonymyeon.backend.notification.domain;

public enum NotificationMessage {

    THUMBS_NOTIFICATION_TITLE("당신의 글에 누군가 반응을 남겼습니다."),
    COMMENT_NOTIFICATION_TITLE("당신의 글에 누군가 댓글을 남겼습니다."),
    UNCONFIRMED_POST_REMINDER_TITLE("소비/절약확정하지 않은 게시글이 있어요! 확인해주세요");

    private final String message;

    NotificationMessage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
