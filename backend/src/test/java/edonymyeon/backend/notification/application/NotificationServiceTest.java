package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class NotificationServiceTest extends IntegrationFixture {

    private final NotificationService notificationService;

    @Test
    void 알림을_성공적으로_전송한_이후에는_알림_내역을_저장한다(@Autowired NotificationRepository notificationRepository) {
        final Post post = postTestSupport.builder().build();
        notificationService.sendThumbsNotificationToWriter(post);

        assertThat(notificationRepository.findAll()).hasSize(1);
    }

    @Test
    void 알림_전송에_실패해도_기록으로_남는다(@Autowired NotificationRepository notificationRepository) {
        final Post post = postTestSupport.builder().build();
        org.assertj.core.api.Assertions.assertThatCode(() -> notificationService.sendThumbsNotificationToWriter(post))
                .doesNotThrowAnyException();

        assertThat(notificationRepository.findAll()).hasSize(1);
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

    @Test
    void 소비_절약확정을_하지_않은_사용자에게_쫌_하라는_알림을_보낸다(
            @Autowired NotificationRepository notificationRepository
    ) {
        final Member member = memberTestSupport.builder().build();
        final Post post1 = postTestSupport.builder().member(member).build();

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> assertThat(notificationRepository.findAll()).hasSize(3));
    }
}
