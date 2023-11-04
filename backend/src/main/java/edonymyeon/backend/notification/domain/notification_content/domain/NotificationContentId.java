package edonymyeon.backend.notification.domain.notification_content.domain;

public enum NotificationContentId {

    THUMBS_NOTIFICATION_TITLE(new NotificationContent("누군가 당신의 글에 반응을 남겼습니다.", "반응을 확인해보세요!")),
    THUMBS_PER_10_NOTIFICATION_TITLE(new NotificationContent("누군가 당신의 글에 많은 반응을 남겼습니다.", "반응을 확인해보세요!")),
    COMMENT_NOTIFICATION_TITLE(new NotificationContent("누군가 당신의 글에 댓글을 남겼습니다.", "댓글 내용을 확인해보세요!")),
    UNCONFIRMED_POST_REMINDER_TITLE(new NotificationContent("구매 또는 절약 확정이 되지 않은 글이 있습니다.", "소비를 기록하고 그래프를 예쁘게 꾸며보세요!")),
    ;

    private final NotificationContent defaultContent;

    NotificationContentId(final NotificationContent defaultContent) {
        this.defaultContent = defaultContent;
    }

    public NotificationContent getDefaultContent() {
        return defaultContent;
    }
}
