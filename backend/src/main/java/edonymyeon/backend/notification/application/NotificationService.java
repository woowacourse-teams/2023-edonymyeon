package edonymyeon.backend.notification.application;

import static edonymyeon.backend.notification.domain.NotificationMessageId.COMMENT_NOTIFICATION_TITLE;
import static edonymyeon.backend.notification.domain.NotificationMessageId.THUMBS_NOTIFICATION_TITLE;
import static edonymyeon.backend.notification.domain.NotificationMessageId.THUMBS_PER_10_NOTIFICATION_TITLE;

import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.consumption.application.ConsumptionService;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.notification.application.dto.Data;
import edonymyeon.backend.notification.application.dto.NotificationResponse;
import edonymyeon.backend.notification.application.dto.Receiver;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.NotificationMessageId;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.domain.notification_content.application.NotificationMessageRepository;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.thumbs.application.ThumbsService;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final EntityManager entityManager;

    private final NotificationSender notificationSender;

    private final NotificationRepository notificationRepository;

    private final MemberRepository memberRepository;

    private final SettingService settingService;

    private final ThumbsService thumbsService;

    private final ConsumptionService consumptionService;

    private final NotificationMessageRepository notificationMessageRepository;

    /**
     * 특정 회원이 받은 알림 내역을 조회합니다.
     * @param memberId 알림 내역을 조회할 회원의 식별자
     * @param findingCondition 페이징 조건
     * @return 페이징 처리된 알림 내역
     */
    public PostSlice<NotificationResponse> findNotifications(MemberId memberId,
                                                             final GeneralFindingCondition findingCondition) {
        final Slice<Notification> notificationSlice = notificationRepository.findByMemberId(memberId.id(), findingCondition.toPage());
        final Slice<NotificationResponse> notificationResponses = notificationSlice
                .map(NotificationResponse::from);
        return PostSlice.from(notificationResponses);
    }

    /**
     * 자신의 게시글에 누군가 추천/비추천을 남겼음을 알리는 알림을 발송합니다
     * @param post 추천/비추천이 달린 게시글
     */
    @Async
    @Transactional
    public void sendThumbsNotificationToWriter(final Post post) {
        if (consumptionService.isPostConfirmed(post.getId())) {
            return;
        }

        if (settingService.isSettingActive(new ActiveMemberId(post.getWriterId()), SettingType.NOTIFICATION_PER_10_THUMBS)
                && isDivisibleBy10(thumbsService.countReactions(post.getId()))) {
            sendNotification(post.getMember(), ScreenType.POST, post.getId(), THUMBS_PER_10_NOTIFICATION_TITLE);
            return;
        }

        if (settingService.isSettingActive(new ActiveMemberId(post.getWriterId()), SettingType.NOTIFICATION_PER_THUMBS)) {
            sendNotification(post.getMember(), ScreenType.POST, post.getId(), THUMBS_NOTIFICATION_TITLE);
        }
    }

    private static boolean isDivisibleBy10(final Long reactionCount) {
        return reactionCount % 10 == 0;
    }

    /**
     * 자신의 게시글에 댓글이 달렸음을 알리는 알림을 발송합니다.
     * @param comment 게시글에 달린 댓글
     */
    @Async
    @Transactional
    public void sendCommentNotificationToPostWriter(Comment comment) {
        if (comment.isWrittenSelf()) {
            return;
        }

        if (settingService.isSettingActive(new ActiveMemberId(comment.getPost().getWriterId()), SettingType.NOTIFICATION_PER_COMMENT)) {
            comment = entityManager.merge(comment);

            sendNotification(comment.getPostWriter(), ScreenType.POST, comment.findPostId(), COMMENT_NOTIFICATION_TITLE);
        }
    }

    /**
     * 소비/절약 확정을 하지 않은 게시글 작성자들에게 오후 8시마다 리마인드 알림을 발송합니다.
     */
    // 매일 오후 8시
    @Scheduled(cron = "0 0 20 * * *")
    @Transactional
    public void remindConfirmingConsumptions() {
        final List<Member> membersHavingUnConfirmedPost = memberRepository.findAllHavingUnConfirmedPostWithInDays(31);

        for (Member member : membersHavingUnConfirmedPost) {
            remindConfirmingConsumptions(member);
        }
    }

    private void remindConfirmingConsumptions(final Member member) {
        if (settingService.isSettingActive(new ActiveMemberId(member.getId()),
                SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING)) {
            sendNotification(member, ScreenType.MYPOST, null, NotificationMessageId.UNCONFIRMED_POST_REMINDER_TITLE);
        }
    }

    /**
     * 사용자에게 알림을 전송합니다.
     * @param notifyingTarget 알림을 전송할 대상
     * @param notifyingType 알림을 클릭했을 때 리다이렉트할 페이지의 종류
     * @param redirectId 알림을 클릭했을 때 리다이렉트할 페이지의 id
     * @param notificationMessageId 알림에서 표시할 제목
     */
    private void sendNotification(final Member notifyingTarget, final ScreenType notifyingType, final Long redirectId,
                                  final NotificationMessageId notificationMessageId) {
        if (notifyingTarget.isDeleted()) {
            return;
        }

        final NotificationContent notificationContent
                = notificationMessageRepository.findById(notificationMessageId)
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.NOTIFICATION_MESSAGE_NOT_FOUND));

        final Long notificationId = saveNotification(notifyingTarget, notifyingType, redirectId, notificationContent);

        final Optional<String> deviceToken = notifyingTarget.getActiveDeviceToken();
        if (deviceToken.isEmpty()) {
            return;
        }

        final Receiver receiver = new Receiver(notifyingTarget,
                new Data(notificationId, notifyingType, redirectId));
        try {
            notificationSender.sendNotification(
                    receiver,
                    notificationContent.getTitle()
            );
        } catch (BusinessLogicException e) {
            log.error("알림 전송에 실패했습니다.", e);
        }
    }

    /**
     * 알림 전송 전/후 해당 내용을 저장합니다.
     *
     * @param notifyingTarget       알림을 전송받은 대상
     * @param notifyingType         알림을 클릭했을 때 리다이렉트한 페이지의 종류
     * @param redirectId            알림을 클릭했을 때 리다이렉트한 페이지의 id
     * @param notificationContent   알림에 표시할 제목과 본문
     * @return 알림 식별자
     */
    private Long saveNotification(final Member notifyingTarget, final ScreenType notifyingType, final Long redirectId,
                                  final NotificationContent notificationContent) {

        final Notification notification = new Notification(
                notifyingTarget,
                notificationContent,
                notifyingType,
                redirectId
        );
        return notificationRepository.save(notification).getId();
    }

    /**
     * 알림을 '읽었음' 표시합니다.
     * @param notificationId '읽었음' 표시할 알림의 식별자
     */
    @Async
    @Transactional
    public void markNotificationAsRead(final Long notificationId) {
        final Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(Notification::markAsRead);
    }

    /**
     * 특정 게시글로부터 발생한 알림 내역을 전부 삭제합니다.
     * @param postId 게시글의 식별자
     */
    @Async
    @Transactional
    public void deleteNotificationByPost(final Long postId) {
        notificationRepository.deleteAllByPostId(postId);
    }

    /**
     * 알림 메시지로 보낼 제목과 본문 내용을 변경합니다.
     * @param content 변경할 Notification 정보
     */
    @Transactional
    public void updateContent(final NotificationContent content) {
        final NotificationContent notificationContent = notificationMessageRepository.findById(content.getId())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.NOTIFICATION_MESSAGE_NOT_FOUND));
        notificationContent.update(content);
    }
}
