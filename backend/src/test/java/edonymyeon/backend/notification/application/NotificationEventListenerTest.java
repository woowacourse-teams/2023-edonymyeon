package edonymyeon.backend.notification.application;

import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.MemberTestSupport;
import edonymyeon.backend.support.PostTestSupport;
import edonymyeon.backend.thumbs.application.ThumbsService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class NotificationEventListenerTest {

    @Autowired
    private MemberTestSupport memberTestSupport;

    @Autowired
    private PostTestSupport postTestSupport;

    @Autowired
    private ThumbsService thumbsService;

    @SpyBean
    private NotificationEventListener notificationEventListener;

    @Test
    void 나의_게시글에_따봉을_누르면_알림을_전송한다() {
        final Member member = memberTestSupport.builder().build();
        final Post post = postTestSupport.builder().build();
        thumbsService.thumbsUp(new ActiveMemberId(member.getId()), post.getId());

        Mockito.verify(notificationEventListener, Mockito.atLeast(1)).listen(Mockito.any());
    }

}
