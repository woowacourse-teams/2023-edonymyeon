package edonymyeon.backend.notification.domain.notification_content.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.Map;
import java.util.Map.Entry;
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
    private NotificationContentId id;

    private String title;

    private String body;

    public NotificationContent(final NotificationContentId id, final String title, final String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public NotificationContentId getId() {
        return id;
    }

    /**
     * title 원본에 '%문자' 형태의 네임드 파라미터가 들어있을 때,
     * Map 객체에 파라미터 이름과 치환해 대입할 갑을 넣으면
     * 해당 문자로 파라미터를 치환한 값을 가져올 수 있습니다.
     * 예) [%str] 한 개요~ + Map.of("str", "제목예시") -> [제목예시] 한 개요~
     * @param parameters 치환할 파라미터와 값을 담은 Map
     */
    public String getTitle(Map<String, String> parameters) {
        return applyDSLs(this.title, parameters);
    }

    /**
     * body 원본에 '%문자' 형태의 네임드 파라미터가 들어있을 때,
     * Map 객체에 파라미터 이름과 치환해 대입할 갑을 넣으면
     * 해당 문자로 파라미터를 치환한 값을 가져올 수 있습니다.
     * 예) [%str] 한 개요~ + Map.of("str", "제목예시") -> [제목예시] 한 개요~
     * @param parameters 치환할 파라미터와 값을 담은 Map
     */
    public String getBody(Map<String, String> parameters) {
        return applyDSLs(this.body, parameters);
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

    private String applyDSLs(final String target, final Map<String, String> parameters) {
        String result = target;
        for (Entry<String, String> parameter : parameters.entrySet()) {
            result = result.replaceAll("%%%s".formatted(parameter.getKey()), parameter.getValue());
        }
        return result;
    }
}
