package edonymyeon.backend.notification.domain.notification_content.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.notification.domain.NotificationMessage;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationContent {
    @Id
    @Enumerated(EnumType.STRING)
    private NotificationMessage id;

    private String title;

    private String body;

    public NotificationContent(final NotificationMessage id, final String title, final String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public NotificationMessage getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationContent that = (NotificationContent) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(body,
                that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body);
    }

    public void update(final NotificationContent content) {
        if (Objects.isNull(content.title) && Objects.isNull(content.body)) {
            throw new EdonymyeonException(ExceptionInformation.NOTIFICATION_MESSAGE_INVALID_CONTENT);
        }

        if (Objects.nonNull(content.title)) {
            this.title = content.title;
        }

        if (Objects.nonNull(content.body)) {
            this.body = content.body;
        }
    }
}
