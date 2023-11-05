package edonymyeon.backend.notification.domain.notification_content.application;

import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import edonymyeon.backend.support.IntegrationFixture;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class NotificationContentHolderTest extends IntegrationFixture {

    private final NotificationContentHolder notificationContentHolder;

    @Test
    void 스프링_애플리케이션이_실행되면_DB의_NotificationContents_정보를_캐시에_올린다() {
        for (NotificationContentId notificationContentId : NotificationContentId.values()) {
            Assertions.assertThat(notificationContentHolder.findById(notificationContentId))
                    .isNotNull();
        }
    }
}
