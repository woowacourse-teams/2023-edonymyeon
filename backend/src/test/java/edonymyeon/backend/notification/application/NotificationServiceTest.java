package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.comment.application.CommentService;
import edonymyeon.backend.comment.application.dto.request.CommentRequest;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.thumbs.application.ThumbsService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class NotificationServiceTest extends IntegrationFixture {

    private final NotificationService notificationService;

    private final AuthService authService;

    private final SettingService settingService;

    private final NotificationRepository notificationRepository;

    @Test
    void 알림을_성공적으로_전송한_이후에는_알림_내역을_저장한다() {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), new ActiveMemberId(writer.getId()));
        final Post post = postTestSupport.builder().member(writer).build();
        notificationService.sendThumbsNotificationToWriter(post);

        assertThat(notificationRepository.findAll()).hasSize(1);
    }

    @Test
    void 알림_전송에_실패해도_기록으로_남는다() {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), new ActiveMemberId(writer.getId()));
        final Post post = postTestSupport.builder().member(writer).build();

        doThrow(new BusinessLogicException(ExceptionInformation.NOTIFICATION_REQUEST_FAILED))
                .when(notificationSender)
                .sendNotification(any(), any());

        assertThatCode(() -> notificationService.sendThumbsNotificationToWriter(post))
                .doesNotThrowAnyException();

        assertThat(notificationRepository.findAll()).hasSize(1);
    }

    @Test
    void 특정_알림을_사용자가_읽었음을_체크할_수_있다() {
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
    void 좋아요_관련_알림이_비활성화_되어_있다면_알림을_보내지_않는다(@Autowired ThumbsService thumbsService) {
        final Member writer = getJoinedMember(authService);
        final Member liker = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        thumbsService.thumbsUp(new ActiveMemberId(liker.getId()), post.getId());

        verify(super.notificationSender, never()).sendNotification(any(), any());
    }

    @Test
    void 건별_당_좋아요_알림이_활성화되어_있다면_좋아요가_달릴_때마다_알림을_발송한다(@Autowired ThumbsService thumbsService) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), new ActiveMemberId(writer.getId()));

        final Member liker = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        thumbsService.thumbsUp(new ActiveMemberId(liker.getId()), post.getId());

        verify(super.notificationSender, atLeastOnce()).sendNotification(any(), any());
    }

    @Test
    void 열건_당_좋아요_알림이_활성화되어_있다면_좋아요가_10개_달릴_때마다_알림을_발송한다(@Autowired ThumbsService thumbsService) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), new ActiveMemberId(writer.getId()));

        final Post post = postTestSupport.builder().member(writer).build();

        for (int i = 0; i < 10; i++) {
            thumbsService.thumbsUp(new ActiveMemberId(memberTestSupport.builder().build().getId()), post.getId());
        }

        verify(super.notificationSender, times(1)).sendNotification(any(), any());
    }

    @Test
    void 댓글당_알림이_비활성화되어_있다면_알림을_받지_않는다(@Autowired CommentService commentService) {
        final Member writer = getJoinedMember(authService);

        final Member commenter = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        commentService.createComment(new ActiveMemberId(commenter.getId()), post.getId(),
                new CommentRequest(null, "Test Commentary"));

        verify(super.notificationSender, never()).sendNotification(any(), any());
    }

    @Test
    void 댓글당_알림이_활성화되어_있다면_알림을_발송한다(@Autowired CommentService commentService) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(), new ActiveMemberId(writer.getId()));

        final Member commenter = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        commentService.createComment(new ActiveMemberId(commenter.getId()), post.getId(),
                new CommentRequest(null, "Test Commentary"));

        verify(super.notificationSender, atLeastOnce()).sendNotification(any(), any());
    }

    @Test
    void 리마인드_알림이_비활성화되어_있다면_알림을_받지_않는다(@Autowired AuthService authService) {
        final Member member = getJoinedMember(authService);
        postTestSupport.builder().member(member).build();

        await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> verify(notificationSender, never()).sendNotification(any(), any()));
    }

    @Test
    void 탈퇴한_회원에게는_알림을_발송하면_안된다(
            @Autowired AuthService authService,
            @Autowired CommentService commentService,
            @Autowired MemberService memberService
    ) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(),
                new ActiveMemberId(writer.getId()));

        final Member commenter = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        memberService.deleteMember(writer.getId());

        commentService.createComment(new ActiveMemberId(commenter.getId()), post.getId(),
                new CommentRequest(null, "Test Commentary"));

        verify(super.notificationSender, never()).sendNotification(any(), any());
    }

    @Test
    void 삭제한_게시글에_대한_알림을_발송하면_안된다(
            @Autowired AuthService authService,
            @Autowired PostService postService
    ) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING.getSerialNumber(),
                new ActiveMemberId(writer.getId()));

        final Post post = postTestSupport.builder().member(writer).build();

        postService.deletePost(new ActiveMemberId(writer.getId()), post.getId());
        notificationService.remindConfirmingConsumptions();

        verify(super.notificationSender, never()).sendNotification(any(), any());
    }

    @Test
    void 게시글이_삭제되면_그와_관련된_알림_내역도_함께_삭제된다(
            @Autowired AuthService authService,
            @Autowired CommentService commentService,
            @Autowired PostService postService
    ) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(),
                new ActiveMemberId(writer.getId()));

        final Member commenter = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        commentService.createComment(new ActiveMemberId(commenter.getId()), post.getId(),
                new CommentRequest(null, "Test Commentary"));

        assertThat(notificationRepository.count()).isEqualTo(1);

        postService.deletePost(new ActiveMemberId(writer.getId()), post.getId());

        assertThat(notificationRepository.count()).isZero();
    }

    @Test
    void 자신의_게시글에_자신이_댓글을_남기면_알림이_가지_않는다(
            @Autowired AuthService authService,
            @Autowired CommentService commentService
    ) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(),
                new ActiveMemberId(writer.getId()));

        final Post post = postTestSupport.builder().member(writer).build();

        commentService.createComment(new ActiveMemberId(writer.getId()), post.getId(),
                new CommentRequest(null, "Test Commentary"));

        Assertions.assertAll(
                () -> verify(notificationSender, never()).sendNotification(any(), any()),
                () -> assertThat(notificationRepository.count()).isZero()
        );
    }

    @Test
    void 로그아웃한_사용자에게_알림을_보내지_않는다(@Autowired CommentService commentService) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(), new ActiveMemberId(writer.getId()));

        final Member commenter = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().member(writer).build();

        authService.logout(writer.getActiveDeviceToken().orElseGet(() -> fail("활성화된 디바이스 토큰이 존재하지 않음")));

        commentService.createComment(new ActiveMemberId(commenter.getId()), post.getId(),
                new CommentRequest(null, "Test Commentary"));

        Assertions.assertAll(
                () -> verify(notificationSender, never()).sendNotification(any(), any()),
                () -> assertThat(notificationRepository.count())
                        .as("알림은 발송하지 않았더라도 알림 내역은 저장해야지!")
                        .isEqualTo(1)
        );
    }

    @Test
    void 소비절약_확정이_완료된_게시글에는_반응알림을_보내지_않는다(
            @Autowired MemberService memberService,
            @Autowired ThumbsService thumbsService
    ) {
        final Member writer = getJoinedMember(authService);
        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), new ActiveMemberId(writer.getId()));

        final Post post = postTestSupport.builder().member(writer).build();
        memberService.confirmPurchase(new ActiveMemberId(writer.getId()), post.getId(),
                new PurchaseConfirmRequest(4000L, 2023, 8));

        final Member liker = memberTestSupport.builder().build();

        thumbsService.thumbsUp(new ActiveMemberId(liker.getId()), post.getId());

        Assertions.assertAll(
                () -> verify(notificationSender, never()).sendNotification(any(), any()),
                () -> assertThat(notificationRepository.count()).isZero()
        );
    }

    private static Member getJoinedMember(final AuthService authService) {
        return authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "backfoxxx", "testDevice123"));
    }
}
