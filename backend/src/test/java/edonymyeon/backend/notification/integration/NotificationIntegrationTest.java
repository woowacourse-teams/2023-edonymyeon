package edonymyeon.backend.notification.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.notification.application.dto.NotificationsResponse;
import edonymyeon.backend.notification.infrastructure.FCMNotificationSender;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.thumbs.application.ThumbsService;
import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@IntegrationTest
public class NotificationIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    private final ThumbsService thumbsService;

    @MockBean
    private FCMNotificationSender notificationSender;

    @BeforeEach
    void 알림전송기능을_모킹한다() {
        when(notificationSender.sendNotification(any(), any())).thenReturn(true);
    }

    @Test
    void 사용자가_받은_알림_목록을_조회한다() {
        final var 글쓴이 = 사용자를_하나_만든다();
        final var 열람인 = 사용자를_하나_만든다();
        final var 열람인2 = 사용자를_하나_만든다();
        final var 게시글id = 응답의_location헤더에서_id를_추출한다(게시글을_하나_만든다(글쓴이));

        thumbsService.thumbsUp(new ActiveMemberId(열람인.getId()), 게시글id);
        thumbsService.thumbsDown(new ActiveMemberId(열람인2.getId()), 게시글id);

        final var 알림목록_조회결과 = RestAssured.given()
                .auth().preemptive().basic(글쓴이.getEmail(), 글쓴이.getPassword())
                .when()
                .get("/notification")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(NotificationsResponse.class);

        assertThat(알림목록_조회결과.notifications()).hasSize(2);
    }
}
