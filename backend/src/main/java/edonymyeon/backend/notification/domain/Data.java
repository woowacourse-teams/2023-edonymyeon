package edonymyeon.backend.notification.domain;

import lombok.Getter;

@Getter
public class Data {
    private String navigateTo;
    private Long postId;
    private boolean read;

    public Data(final ScreenType navigateTo, final Long postId) {
        this.navigateTo = navigateTo.name();
        this.postId = postId;
        this.read = false;
    }
}
