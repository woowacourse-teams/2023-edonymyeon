package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.*;
import static org.mockito.Mockito.when;

import edonymyeon.backend.consumption.application.ConsumptionService;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.NotificationMessageId;
import edonymyeon.backend.notification.domain.notification_content.application.NotificationMessageRepository;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.thumbs.application.ThumbsService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class NotificationService_NotificationMessageTest extends IntegrationFixture {

    private NotificationService notificationService;
    private final NotificationMessageRepository notificationMessageRepository;

    public NotificationService_NotificationMessageTest(final NotificationService notificationService,
                                                       final NotificationMessageRepository notificationMessageRepository) {
        this.notificationService = notificationService;
        this.notificationMessageRepository = notificationMessageRepository;
    }

    @MockBean
    ConsumptionService consumptionService;
    @MockBean
    SettingService settingService;

    @BeforeEach
    void setup() {
        notificationMessageRepository.save(
                new NotificationContent(NotificationMessageId.THUMBS_NOTIFICATION_TITLE, "당신의 글에 따봉이 달려있어요! 짱",
                        "따봉맨을 확인해 보세요."));
    }

    @Test
    void 알림에_사용할_메시지는_리포지토리에서_조회하여_사용한다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);
        주요기능모킹하기(member, post);

        // when
        notificationService.sendThumbsNotificationToWriter(post);

        // then
        final Notification savedNotification = notificationRepository
                .findByMemberId(member.getId(), Pageable.ofSize(1)).getContent()
                .get(0);

        final NotificationContent expectedNotificationContent = notificationMessageRepository.findById(
                NotificationMessageId.THUMBS_NOTIFICATION_TITLE).get();

        assertThat(savedNotification.getBody())
                .as("리포지토리에 저장한 알림 title 내용으로 알림을 발송해야 한다.")
                .isEqualTo(expectedNotificationContent.getBody());

        assertThat(savedNotification.getTitle())
                .as("리포지토리에 저장한 알림 body 내용으로 알림을 발송해야 한다.")
                .isEqualTo(expectedNotificationContent.getTitle());
    }

    @Test
    void 알림에_사용할_메시지가_존재하지_않는_경우_예외가_발생한다(
            @Autowired EntityManager entityManager,
            @Autowired NotificationRepository notificationRepository,
            @Autowired MemberRepository memberRepository,
            @Autowired ThumbsService thumbsService,
            @Mock NotificationMessageRepository notificationMessageRepository
    ) {
        //given
        this.notificationService = new NotificationService(
                entityManager, super.notificationSender, notificationRepository, memberRepository,
                settingService, thumbsService, consumptionService, notificationMessageRepository
        );

        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);

        주요기능모킹하기(member, post);
        when(notificationMessageRepository.findById(NotificationMessageId.THUMBS_NOTIFICATION_TITLE))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.sendThumbsNotificationToWriter(post))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(ExceptionInformation.NOTIFICATION_MESSAGE_NOT_FOUND.getMessage());
    }

    @Test
    void 특정_알림에서_사용할_제목_또는_내용을_수정할_수_있다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);
        주요기능모킹하기(member, post);

        notificationService.sendThumbsNotificationToWriter(post);
        final Notification savedNotification = notificationRepository
                .findAll()
                .get(0);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(savedNotification.getTitle()).isEqualTo("당신의 글에 따봉이 달려있어요! 짱");
            softAssertions.assertThat(savedNotification.getBody()).isEqualTo("따봉맨을 확인해 보세요.");
        });

        // when
        final NotificationContent notificationContent
                = new NotificationContent(NotificationMessageId.THUMBS_NOTIFICATION_TITLE, "변경한 알림 제목", "변경한 알림 내용");
        notificationService.updateContent(notificationContent);

        // then
        notificationService.sendThumbsNotificationToWriter(post); // 따봉을 취소했다가 다시 한 상황
        final Notification updatedNotification = notificationRepository
                .findAll()
                .get(1);
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(updatedNotification.getTitle()).isEqualTo("변경한 알림 제목");
            softAssertions.assertThat(updatedNotification.getBody()).isEqualTo("변경한 알림 내용");
        });
    }

    @Test
    void 알림_메시지가_수정된_이후_새로_등록된_알림에만_변경_사항이_적용된다(
            @Autowired NotificationRepository notificationRepository
    ) {
        // given
        final Member member = 회원만들기();
        final Post post = 게시글만들기(member);
        주요기능모킹하기(member, post);

        notificationService.sendThumbsNotificationToWriter(post);

        // when
        final NotificationContent notificationContent
                = new NotificationContent(NotificationMessageId.THUMBS_NOTIFICATION_TITLE, "변경한 알림 제목", "변경한 알림 내용");
        notificationService.updateContent(notificationContent);

        // then
        notificationService.sendThumbsNotificationToWriter(post); // 따봉을 취소했다가 다시 한 상황
        final List<Notification> savedNotification = notificationRepository.findAll();
        Assertions.assertAll("이전에 기록된 알림의 제목과 본문 내용은 변경이 없어야 한다.", () -> {
            assertThat(savedNotification.get(0).getTitle()).isEqualTo("당신의 글에 따봉이 달려있어요! 짱");
            assertThat(savedNotification.get(0).getBody()).isEqualTo("따봉맨을 확인해 보세요.");
        });

        Assertions.assertAll("메시지가 변경된 이후 발송한 알림에만 제목과 본문에 변경 사항이 반영된다.", () -> {
            assertThat(savedNotification.get(1).getTitle()).isEqualTo("변경한 알림 제목");
            assertThat(savedNotification.get(1).getBody()).isEqualTo("변경한 알림 내용");
        });
    }

    private void 주요기능모킹하기(final Member member, final Post post) {
        when(consumptionService.isPostConfirmed(post.getId())).thenReturn(false);
        when(settingService.isSettingActive(new ActiveMemberId(member.getId()),
                SettingType.NOTIFICATION_PER_10_THUMBS)).thenReturn(false);
        when(settingService.isSettingActive(new ActiveMemberId(member.getId()),
                SettingType.NOTIFICATION_PER_THUMBS)).thenReturn(true);
    }

    @NotNull
    private static Post 게시글만들기(final Member member) {
        return new Post(1L, "신발 사도 될까요?", "사게 해주십시오!", 5000L, member);
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
}
