package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.comment.application.CommentService;
import edonymyeon.backend.comment.application.dto.request.CommentRequest;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.thumbs.application.ThumbsService;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@RunWith(MockitoJUnitRunner.class)
class NotificationEventListenerTest extends IntegrationFixture {

    @SpyBean
    private NotificationEventListener notificationEventListener;
    private final AuthService authService;
    private final SettingService settingService;
    private final ThumbsService thumbsService;
    private final NotificationRepository notificationRepository;
    private final ThumbsRepository thumbsRepository;

    @Test
    void 나의_게시글에_따봉을_누르면_알림을_전송한다() {
        doNothing().when(notificationEventListener).sendThumbsUpNotification(any());

        final Member member = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().build();
        thumbsService.thumbsUp(new ActiveMemberId(member.getId()), post.getId());

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> verify(notificationEventListener, Mockito.atLeast(1)).sendThumbsUpNotification(Mockito.any()));
    }

    @Test
    void 따봉_저장이_완료된_후에는_알림_발송_후_저장한다() {
        final Member liker = 사용자를_하나_만든다();
        final Member writer = authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "backfoxxx", "testDevice123"));
        final Post post = postTestSupport.builder().member(writer).build();
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), new ActiveMemberId(writer.getId()));

        thumbsService.thumbsUp(new ActiveMemberId(liker.getId()), post.getId());

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertThat(notificationRepository.findAll())
                            .as("알림도 저장하고")
                            .hasSize(1);

                    final List<Thumbs> thumbs = thumbsRepository.findByPostId(post.getId());
                    assertThat(thumbs)
                            .as("따봉도 정상적으로 저장되어야 한다.")
                            .hasSize(1);
                });
    }

    @Test
    void 댓글을_남기면_글_작성자에게_알림이_간다(
            @Autowired TransactionTemplate template,
            @Autowired CommentService commentService
    ) {
        final Member writer = authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "backfoxxx", "testDevice123"));
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(), new ActiveMemberId(writer.getId()));
        final Member commenter = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();
        final CommentRequest commentRequest = new CommentRequest(null, "뭐 이런 글을 썼대요");
        commentService.createComment(new ActiveMemberId(commenter.getId()), post.getId(), commentRequest);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    template.execute(status -> {
                        final List<Notification> notifications = notificationRepository.findAll();
                        assertThat(notifications).hasSize(1);
                        assertThat(notifications.get(0).getMemberId()).isEqualTo(writer.getId());
                        return "";
                    });
                });
    }
}
