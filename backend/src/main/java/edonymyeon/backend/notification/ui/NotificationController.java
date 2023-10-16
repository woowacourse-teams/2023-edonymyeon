package edonymyeon.backend.notification.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.notification.application.NotificationService;
import edonymyeon.backend.notification.application.dto.NotificationResponse;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.ui.annotation.PostPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @ApiVersion(value = {1, 2})
    @GetMapping("/notification")
    public ResponseEntity<PostSlice<NotificationResponse>> findNotifications(@AuthPrincipal MemberId memberId, @PostPaging GeneralFindingCondition generalFindingCondition) {
        return ResponseEntity.ok()
                .body(notificationService.findNotifications(memberId, generalFindingCondition));
    }
}
