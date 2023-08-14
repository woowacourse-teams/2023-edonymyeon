package edonymyeon.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.thumbs.application.ThumbsService;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SuppressWarnings("NonAsciiCharacters")
@IntegrationTest
@RequiredArgsConstructor
@RunWith(MockitoJUnitRunner.class)
class NotificationEventListenerTest extends IntegrationFixture {

    private final MemberTestSupport memberTestSupport;

    private final PostTestSupport postTestSupport;

    private final ThumbsService thumbsService;

    @MockBean
    private NotificationSender notificationSender;

    @SpyBean
    private NotificationEventListener notificationEventListener;

    @Test
    void 나의_게시글에_따봉을_누르면_알림을_전송한다() {
        doNothing().when(notificationEventListener).sendThumbsUpNotification(any());

        final Member member = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().build();
        thumbsService.thumbsUp(new ActiveMemberId(member.getId()), post.getId());

        Mockito.verify(notificationEventListener, Mockito.atLeast(1)).sendThumbsUpNotification(Mockito.any());
    }

    @Test
    void 따봉_저장이_완료된_후에는_알림_발송_후_저장한다(
            @Autowired ThumbsService thumbsService,
            @Autowired NotificationRepository notificationRepository,
            @Autowired ThumbsRepository thumbsRepository
    ) {
        when(notificationSender.sendNotification(any(), any())).thenReturn(true);

        final Member member = 사용자를_하나_만든다();
        final Post post = postTestSupport.builder().build();
        thumbsService.thumbsUp(new ActiveMemberId(member.getId()), post.getId());

        assertThat(notificationRepository.findAll())
                .as("알림도 저장하고")
                .hasSize(1);

        final List<Thumbs> thumbs = thumbsRepository.findByPostId(post.getId());
        assertThat(thumbs)
                .as("따봉도 정상적으로 저장되어야 한다.")
                .hasSize(1);
    }

    @Test
    void 알림_전송_트랜잭션이_실패했다고_해서_따봉까지_롤백되어서는_안된다(
            @Autowired ThumbsService thumbsService,
            @Autowired NotificationRepository notificationRepository,
            @Autowired ThumbsRepository thumbsRepository
    ) {
        when(notificationSender.sendNotification(any(), any())).thenReturn(false);

        final Member member = 사용자를_하나_만든다();
        final Post post = postTestSupport.builder().build();
        thumbsService.thumbsUp(new ActiveMemberId(member.getId()), post.getId());

        assertThat(notificationRepository.findAll())
                .as("알림은 저장되지 않았지만")
                .hasSize(0);

        final List<Thumbs> thumbs = thumbsRepository.findByPostId(post.getId());
        assertThat(thumbs)
                .as("따봉은 정상적으로 저장되어야 한다.")
                .hasSize(1);
    }
}
