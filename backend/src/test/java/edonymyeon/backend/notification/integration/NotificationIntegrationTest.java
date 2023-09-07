package edonymyeon.backend.notification.integration;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.notification.application.dto.NotificationsResponse;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.repository.NotificationRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.thumbs.application.ThumbsService;
import io.restassured.RestAssured;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
public class NotificationIntegrationTest extends IntegrationFixture implements ImageFileCleaner {

    private final ThumbsService thumbsService;

    @Test
    void 사용자가_받은_알림_목록을_조회한다(@Autowired MemberRepository memberRepository, @Autowired NotificationRepository notificationRepository) {
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
                .as(PostSlice.class);

        assertThat(알림목록_조회결과.getContent()).hasSize(2);
        assertThat(알림목록_조회결과.isLast()).isTrue();
    }

    @Test
    void 알림을_통해_페이지에_접속한_경우_알림_정보에_해당_사실을_기록한다(@Autowired NotificationRepository notificationRepository) {
        final Member 사용자 = 사용자를_하나_만든다();
        final long 게시글id = 응답의_location헤더에서_id를_추출한다(게시글을_하나_만든다(사용자));
        final Long 알림id = notificationRepository
                .save(new Notification(사용자, "알림이 등록되었어요!", ScreenType.POST, 게시글id))
                .getId();

        var notification = notificationRepository.findById(알림id);
        notification.ifPresentOrElse(
                not -> assertThat(not.isRead()).isFalse(),
                Assertions::fail);

        RestAssured.given()
                .when()
                .get("/posts/{postId}?notificated={notificationId}",
                        Map.of("postId", 게시글id, "notificationId", 알림id))
                .then()
                .statusCode(HttpStatus.OK.value());

        notification = notificationRepository.findById(알림id);
        notification.ifPresentOrElse(
                not -> assertThat(not.isRead()).isTrue(),
                Assertions::fail);
    }
}
