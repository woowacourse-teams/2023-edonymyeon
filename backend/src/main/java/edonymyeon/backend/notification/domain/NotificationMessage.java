package edonymyeon.backend.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class NotificationMessage {

    @Column
    private String title;

    @Column
    private String body;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
