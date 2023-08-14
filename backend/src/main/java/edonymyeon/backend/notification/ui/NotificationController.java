package edonymyeon.backend.notification.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.notification.application.NotificationService;
import edonymyeon.backend.notification.application.dto.NotificationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification")
    public ResponseEntity<NotificationsResponse> findNotifications(@AuthPrincipal MemberId memberId) {
        return ResponseEntity.ok()
                .body(notificationService.findNotifications(memberId));
    }
}
