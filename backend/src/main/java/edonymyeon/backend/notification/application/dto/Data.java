package edonymyeon.backend.notification.application.dto;

import edonymyeon.backend.notification.domain.ScreenType;
import lombok.Getter;

@Getter
public class Data {
    private String notificationId;
    private String navigateTo;
    private String postId;
    private String read;

    public Data(final Long notificationId, final ScreenType navigateTo, final Long postId) {
        this.notificationId = String.valueOf(notificationId);
        this.navigateTo = navigateTo.name();
        this.postId = String.valueOf(postId);
        this.read = "false";
    }
}
