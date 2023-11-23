package edonymyeon.backend.notification.domain.notification_content.application;

import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DataJpaTest
class NotificationContentHolderTest {

    @Autowired
    private NotificationContentRepository notificationContentRepository;

    private NotificationContentHolder notificationContentHolder;

    @BeforeEach
    void setup() {
        this.notificationContentHolder = new NotificationContentHolder(notificationContentRepository);
    }

    @Test
    void DB에_알림_메시지가_저장되어_있는_경우_DB의_메시지를_가져와_캐시에_올린다() {
        final NotificationContent notificationContent = new NotificationContent(NotificationContentId.THUMBS_NOTIFICATION_TITLE, "title saved in database", "body saved in database");
        notificationContentRepository.save(notificationContent);

        final NotificationContent cachedContent = notificationContentHolder.findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE);
        assertThat(cachedContent.getTitle(Map.of())).isEqualTo("title saved in database");
        assertThat(cachedContent.getBody(Map.of())).isEqualTo("body saved in database");
    }

    @Test
    void DB에_알림_메시지가_없는_경우_기본값으로_등록된_메시지를_캐시에_올린다() {
        final NotificationContent cachedContent = notificationContentHolder.findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE);

        final NotificationContent defaultContent = NotificationContentId.THUMBS_NOTIFICATION_TITLE.getDefaultContent();
        assertThat(cachedContent.getTitle(Map.of())).isEqualTo(defaultContent.getTitle(Map.of()));
        assertThat(cachedContent.getBody(Map.of())).isEqualTo(defaultContent.getBody(Map.of()));
    }

    @Test
    void 알림_메시지를_업데이트하면_DB에_업데이트된_알림_메시지가_반영된다() {
        final NotificationContent notificationContent = new NotificationContent(NotificationContentId.THUMBS_NOTIFICATION_TITLE, "title saved in database", "body saved in database");
        notificationContentRepository.save(notificationContent);

        notificationContentHolder.updateMessage(
                new NotificationContent(
                        NotificationContentId.THUMBS_NOTIFICATION_TITLE,
                        "updated title",
                        "updated body"
                )
        );

        final NotificationContent cachedContent = notificationContentHolder.findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE);
        assertThat(cachedContent.getTitle(Map.of())).isEqualTo("updated title");
        assertThat(cachedContent.getBody(Map.of())).isEqualTo("updated body");

        final NotificationContent savedContent = notificationContentRepository.findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE).orElseThrow();
        assertThat(savedContent.getTitle(Map.of())).isEqualTo("updated title");
        assertThat(savedContent.getBody(Map.of())).isEqualTo("updated body");
    }

    @Test
    void 알림_메시지를_업데이트하면_캐시에도_업데이트_메시지가_반영된다() {
        final NotificationContentRepository mockedRepository = mock(NotificationContentRepository.class);
        this.notificationContentHolder = new NotificationContentHolder(mockedRepository);

        when(mockedRepository.findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE))
                .thenReturn(
                        Optional.of(new NotificationContent(
                                NotificationContentId.THUMBS_NOTIFICATION_TITLE,
                                NotificationContentId.THUMBS_NOTIFICATION_TITLE.getDefaultContent().getTitle(Map.of()),
                                NotificationContentId.THUMBS_NOTIFICATION_TITLE.getDefaultContent().getBody(Map.of())
                        ))
                );

        notificationContentHolder.updateMessage(
                new NotificationContent(
                        NotificationContentId.THUMBS_NOTIFICATION_TITLE,
                        "updated title",
                        "updated body"
                )
        );

        final NotificationContent cachedContent = notificationContentHolder.findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE);
        assertThat(cachedContent.getTitle(Map.of())).isEqualTo("updated title");
        assertThat(cachedContent.getBody(Map.of())).isEqualTo("updated body");

        verify(mockedRepository, times(1)).findById(NotificationContentId.THUMBS_NOTIFICATION_TITLE);
    }
}
