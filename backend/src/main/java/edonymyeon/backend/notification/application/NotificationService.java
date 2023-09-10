package edonymyeon.backend.notification.application;

import static edonymyeon.backend.notification.domain.NotificationMessage.COMMENT_NOTIFICATION_TITLE;
import static edonymyeon.backend.notification.domain.NotificationMessage.THUMBS_NOTIFICATION_TITLE;

import edonymyeon.backend.comment.domain.Comment;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.notification.application.dto.Data;
import edonymyeon.backend.notification.application.dto.NotificationResponse;
import edonymyeon.backend.notification.application.dto.Receiver;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.NotificationMessage;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
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

    public PostSlice<NotificationResponse> findNotifications(MemberId memberId,
                                                             final GeneralFindingCondition findingCondition) {
        final Slice<Notification> notificationSlice = notificationRepository.findByMemberId(memberId.id(), findingCondition.toPage());
        final Slice<NotificationResponse> notificationResponses = notificationSlice
                .map(NotificationResponse::from);
        return PostSlice.from(notificationResponses);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendThumbsNotificationToWriter(final Post post) {
        final Optional<String> deviceToken = post.getMember().getActiveDeviceToken();
        if (deviceToken.isEmpty()) {
            return;
        }

        sendNotification(post.getMember(), ScreenType.POST, post.getId(), THUMBS_NOTIFICATION_TITLE);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendCommentNotificationToPostWriter(Comment comment) {
        comment = entityManager.merge(comment);

        final Optional<String> deviceToken = comment.getDeviceTokenFromPostWriter();
        if (deviceToken.isEmpty()) {
            return;
        }

        sendNotification(comment.getWriter(), ScreenType.POST, comment.findPostId(), COMMENT_NOTIFICATION_TITLE);
    }

    @Scheduled(cron = "${scheduler.cron.reminder}")
    @Transactional(readOnly = true)
    public void remindConfirmingConsumptions() {
        final List<Member> membersHavingUnConfirmedPost = memberRepository.findAllHavingUnConfirmedPost();

        for (Member member : membersHavingUnConfirmedPost) {
            sendNotification(member, ScreenType.MYPOST, null, NotificationMessage.UNCONFIRMED_POST_REMINDER_TITLE);
        }
    }

    private void sendNotification(final Member notifyingTarget, final ScreenType notifyingType, final Long redirectId,
                                  final NotificationMessage notificationMessage) {
        final Long notificationId = saveNotification(notifyingTarget, notifyingType, redirectId, notificationMessage);

        final Receiver receiver = new Receiver(notifyingTarget,
                new Data(notificationId, notifyingType, redirectId));
        try {
            notificationSender.sendNotification(
                    receiver,
                    notificationMessage.getMessage()
            );
        } catch (BusinessLogicException e) {
            log.error("알림 전송에 실패했습니다.", e);
        }
    }

    private Long saveNotification(final Member notifyingTarget, final ScreenType notifyingType, final Long redirectId,
                          final NotificationMessage notificationMessage) {
        final Notification notification = new Notification(
                notifyingTarget,
                notificationMessage.getMessage(),
                notifyingType,
                redirectId
        );
        return notificationRepository.save(notification).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markNotificationAsRead(final Long notificationId) {
        final Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(Notification::markAsRead);
    }
}
