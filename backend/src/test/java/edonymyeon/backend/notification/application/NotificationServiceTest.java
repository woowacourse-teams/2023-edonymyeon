package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edonymyeon.backend.notification.infrastructure.FCMNotificationSender;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.PostTestSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
class NotificationServiceTest {

    private final PostTestSupport postTestSupport;

    private final NotificationService notificationService;

    @MockBean
    private FCMNotificationSender notificationSender;

    @BeforeEach
    void 알림전송기능을_모킹한다() {
        when(notificationSender.sendNotification(any(), any())).thenReturn(true);
    }

    @Test
    void 알림을_성공적으로_전송한_이후에는_알림_내역을_저장한다(@Autowired NotificationRepository notificationRepository) {
        final Post post = postTestSupport.builder().build();
        notificationService.sendThumbsNotificationToWriter(post);

        assertThat(notificationRepository.findAll()).hasSize(1);
    }
}
