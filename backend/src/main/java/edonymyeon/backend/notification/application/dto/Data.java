package edonymyeon.backend.notification.application.dto;

import edonymyeon.backend.notification.domain.ScreenType;
import lombok.Getter;

@Getter
public class Data {
    private Long notificationId;
    private String navigateTo;
    private Long postId;
    private boolean read;

    public Data(final Long notificationId, final ScreenType navigateTo, final Long postId) {
        this.notificationId = notificationId;
        this.navigateTo = navigateTo.name();
        this.postId = postId;
        this.read = false;
    }
}
