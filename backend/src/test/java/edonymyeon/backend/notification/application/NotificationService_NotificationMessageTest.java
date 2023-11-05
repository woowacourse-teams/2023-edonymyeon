package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.consumption.application.ConsumptionService;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.IntegrationFixture;
import java.time.Duration;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class NotificationService_NotificationMessageTest extends IntegrationFixture {

    private final NotificationService notificationService;

    public NotificationService_NotificationMessageTest(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MockBean
    ConsumptionService consumptionService;
    @MockBean
    SettingService settingService;

    @Test
    void 특정_알림에서_사용할_제목_또는_내용을_수정할_수_있다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);
        주요기능모킹하기(member, post);

        notificationService.sendThumbsNotificationToWriter(post);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    final Notification savedNotification = notificationRepository
                            .findAll()
                            .get(0);
                    assertSoftly(softAssertions -> {
                        softAssertions.assertThat(savedNotification.getTitle()).isEqualTo("누군가 당신의 글에 반응을 남겼습니다.");
                        softAssertions.assertThat(savedNotification.getBody()).isEqualTo("반응을 확인해보세요!");
                    });
                });

        // when
        final NotificationContent notificationContent
                = new NotificationContent(NotificationContentId.THUMBS_NOTIFICATION_TITLE, "변경한 알림 제목", "변경한 알림 내용");
        notificationService.updateContent(notificationContent);

        // then
        notificationService.sendThumbsNotificationToWriter(post); // 따봉을 취소했다가 다시 한 상황

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    final Notification updatedNotification = notificationRepository
                            .findAll()
                            .get(1);
                    assertSoftly(softAssertions -> {
                        softAssertions.assertThat(updatedNotification.getTitle()).isEqualTo("변경한 알림 제목");
                        softAssertions.assertThat(updatedNotification.getBody()).isEqualTo("변경한 알림 내용");
                    });
                });
    }

    @Test
    void 알림_메시지_중_title_부분을_게시글_제목_이름으로_치환한다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        notificationService.updateContent(
                new NotificationContent(NotificationContentId.THUMBS_NOTIFICATION_TITLE,
                        "[%title] 글에 새로운 반응이 있습니다.",
                        "[%title] 글에 새로운 반응이 있습니다. 본문"));

        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);
        주요기능모킹하기(member, post);

        notificationService.sendThumbsNotificationToWriter(post);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    final List<Notification> savedNotification = notificationRepository.findAll();
                    Assertions.assertAll("%title 부분을 게시글 제목으로 치환한다.", () -> {
                        assertThat(savedNotification.get(0).getTitle()).isEqualTo("[신발 사도 될까요?] 글에 새로운 반응이 있습니다.");
                        assertThat(savedNotification.get(0).getBody()).isEqualTo("[신발 사도 될까요?] 글에 새로운 반응이 있습니다. 본문");
                    });
                });
    }

    @Test
    void 알림_메시지_중_comment_부분을_게시글_댓글_이름으로_치환한다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        notificationService.updateContent(
                new NotificationContent(NotificationContentId.COMMENT_NOTIFICATION_TITLE,
                        "[%title] 글에 [%comment] 댓글이 달렸습니다",
                        "[%title] 글에 [%comment] 댓글이 달렸습니다. 본문"));

        final Member member = 회원만들기();
        final Member member2 = 회원2만들기();
        final Post post = 게시글만들기(member);

        주요기능모킹하기(member, post);

        final Comment comment = commentTestSupport.builder()
                .content("그 돈이면 국밥이 90그릇")
                .post(post)
                .member(member2)
                .build();
        notificationService.sendCommentNotificationToPostWriter(comment);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    final List<Notification> savedNotification = notificationRepository.findAll();
                    Assertions.assertAll("%comment 부분을 댓글 내용으로 치환한다.", () -> {
                        assertThat(savedNotification.get(0).getTitle()).isEqualTo(
                                "[신발 사도 될까요?] 글에 [그 돈이면 국밥이 90그릇] 댓글이 달렸습니다");
                        assertThat(savedNotification.get(0).getBody()).isEqualTo(
                                "[신발 사도 될까요?] 글에 [그 돈이면 국밥이 90그릇] 댓글이 달렸습니다. 본문");
                    });
                });
    }

    @Test
    void 알림_메시지_중_count_부분을_반응의_총_개수로_치환한다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        notificationService.updateContent(
                new NotificationContent(NotificationContentId.THUMBS_NOTIFICATION_TITLE,
                        "[%title] 글에 총 %count건의 반응이 생겼습니다.",
                        "[%title] 글에 총 %count건의 반응이 생겼습니다. 본문"));

        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);
        주요기능모킹하기(member, post);

        notificationService.sendThumbsNotificationToWriter(post);

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    final List<Notification> savedNotification = notificationRepository.findAll();
                    Assertions.assertAll("%count 부분을 게시글 제목으로 치환한다.", () -> {
                        assertThat(savedNotification.get(0).getTitle()).isEqualTo("[신발 사도 될까요?] 글에 총 0건의 반응이 생겼습니다.");
                        assertThat(savedNotification.get(0).getBody()).isEqualTo("[신발 사도 될까요?] 글에 총 0건의 반응이 생겼습니다. 본문");
                        // 실제 따봉 로직을 호출한 것은 아니므로 0건으로 출력된다
                    });
                });
    }

    private void 주요기능모킹하기(final Member member, final Post post) {
        when(consumptionService.isPostConfirmed(post.getId())).thenReturn(false);
        when(settingService.isSettingActive(new ActiveMemberId(member.getId()),
                SettingType.NOTIFICATION_PER_10_THUMBS)).thenReturn(false);
        when(settingService.isSettingActive(new ActiveMemberId(member.getId()),
                SettingType.NOTIFICATION_PER_THUMBS)).thenReturn(true);
        when(settingService.isSettingActive(new ActiveMemberId(member.getId()),
                SettingType.NOTIFICATION_PER_COMMENT)).thenReturn(true);
    }

    @NotNull
    private Post 게시글만들기(final Member member) {
        return postTestSupport.builder()
                .member(member)
                .title("신발 사도 될까요?")
                .content("사게 해주십시오!")
                .price(5000L)
                .build();
    }

    @NotNull
    private Member 회원만들기() {
        final Member member = memberTestSupport.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("testNickName")
                .build();
        member.activateDevice("testDeviceToken");
        return member;
    }

    @NotNull
    private Member 회원2만들기() {
        final Member member = memberTestSupport.builder()
                .id(2L)
                .email("test2@gmail.com")
                .nickname("test2NickName")
                .build();
        member.activateDevice("test2DeviceToken");
        return member;
    }
}
