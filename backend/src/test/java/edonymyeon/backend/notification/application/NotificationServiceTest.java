package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.infrastructure.FCMNotificationSender;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.support.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
class NotificationServiceTest extends IntegrationFixture {

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

    @Test
    void 알림_전송에_실패했다면_알림을_저장하지_않는다(@Autowired NotificationRepository notificationRepository) {
        when(notificationSender.sendNotification(any(), any())).thenReturn(false);

        final Post post = postTestSupport.builder().build();
        assertThatThrownBy(() -> notificationService.sendThumbsNotificationToWriter(post))
                .isInstanceOf(BusinessLogicException.class);

        assertThat(notificationRepository.findAll()).hasSize(0);
    }

    @Test
    void 특정_알림을_사용자가_읽었음을_체크할_수_있다(@Autowired NotificationRepository notificationRepository) {
        final var post = postTestSupport.builder().build();
        final var notificationId = notificationRepository.save(
                        new Notification(post.getMember(), "알림이 도착했어요!", ScreenType.POST, post.getId()))
                .getId();

        var notification = notificationRepository.findById(notificationId);
        notification.ifPresentOrElse(
                not -> assertThat(not.isRead()).isFalse(),
                Assertions::fail);

        notificationService.markNotificationAsRead(notificationId);

        notification = notificationRepository.findById(notificationId);
        notification.ifPresentOrElse(
                not -> assertThat(not.isRead()).isTrue(),
                Assertions::fail
        );
    }
}
