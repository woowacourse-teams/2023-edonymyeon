package edonymyeon.backend.notification.application;

import static edonymyeon.backend.notification.domain.NotificationMessage.THUMBS_NOTIFICATION_TITLE;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.notification.application.dto.Data;
import edonymyeon.backend.notification.application.dto.NotificationResponse;
import edonymyeon.backend.notification.application.dto.Receiver;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.domain.Post;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationSender notificationSender;

    private final NotificationRepository notificationRepository;

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

        final Long notificationId = saveNotification(post);

        final Receiver receiver = new Receiver(post.getMember(),
                new Data(notificationId, ScreenType.POST, post.getId()));
        try {
            notificationSender.sendNotification(
                    receiver,
                    THUMBS_NOTIFICATION_TITLE.getMessage()
            );
        } catch (BusinessLogicException e) {
            log.error("알림 전송에 실패했습니다.", e);
        }
    }

    private Long saveNotification(final Post post) {
        final Notification notification = new Notification(
                post.getMember(),
                THUMBS_NOTIFICATION_TITLE.getMessage(),
                ScreenType.POST,
                post.getId()
        );
        return notificationRepository.save(notification).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markNotificationAsRead(final Long notificationId) {
        final Optional<Notification> notification = notificationRepository.findById(notificationId);
        notification.ifPresent(Notification::markAsRead);
    }
}
