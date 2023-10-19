package edonymyeon.backend.notification.docs;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.application.NotificationService;
import edonymyeon.backend.notification.application.dto.NotificationResponse;
import edonymyeon.backend.notification.domain.Notification;
import edonymyeon.backend.notification.domain.ScreenType;
import edonymyeon.backend.notification.domain.notification_content.application.dto.NotificationContentRequest;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContentId;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.support.DocsTest;
import edonymyeon.backend.support.TestMemberBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
class NotificationDocsTest extends DocsTest {

    private final TestMemberBuilder testMemberBuilder = new TestMemberBuilder(null);

    @MockBean
    NotificationService notificationService;

    public NotificationDocsTest(final MockMvc mockMvc, final ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    @Test
    void 알림을_조회한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder().id(1L).buildWithoutSaving();

        when(notificationService.findNotifications(eq(new ActiveMemberId(회원.getId())), any(GeneralFindingCondition.class)))
                .thenReturn(PostSlice.from(new SliceImpl<>(
                        List.of(
                                NotificationResponse.from(
                                        new Notification(회원, "회원님의 글에 새 댓글이 달렸습니다.", "댓글을 확인해보세요~!", ScreenType.POST,
                                                1L)),
                                NotificationResponse.from(
                                        new Notification(회원, "주문확정되지 않은 게시글이 있습니다.", "주문확정 해보세요~!", ScreenType.MYPOST,
                                                null))
                        )
                )));

        final MockHttpServletRequestBuilder 알림내역_조회_요청 = get("/notification")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-VERSION", "1.0.0", "1.1.0")
                .sessionAttr(USER.getSessionId(), 회원.getId());

        final RestDocumentationResultHandler 문서화 = document("notification",
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("content[].id").description("알림의 고유 식별자"),
                        fieldWithPath("content[].title").description("알림의 제목"),
                        fieldWithPath("content[].navigateTo").description("알림을 클릭하면 이동할 페이지의 종류"),
                        fieldWithPath("content[].postId").description("알림을 클릭해 이동한 페이지에서 필요로 하는 식별자").optional(),
                        fieldWithPath("content[].read").description("알림의 읽음 여부"),
                        fieldWithPath("last").description("마지막 페이지 여부")
                )
        );

        this.mockMvc.perform(알림내역_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 알림메시지를_수정한다() throws Exception {

        doNothing().when(notificationService).updateContent(any());

        final MockHttpServletRequestBuilder 확정_취소_요청 = put("/admin/notification/content")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new NotificationContentRequest(NotificationContentId.COMMENT_NOTIFICATION_TITLE, "변경된 댓글 알림 제목", "변경된 댓글 알림 본문")))
                .header("X-API-VERSION", "1.0.0", "1.1.0");

        final RestDocumentationResultHandler 문서화 = document("notification-updating-message",
                preprocessRequest(prettyPrint()),
                requestFields(
                    fieldWithPath("id").description("알림 메시지를 변경할 항목의 식별자"),
                    fieldWithPath("title").description("변경할 알림 메시지 제목"),
                    fieldWithPath("body").description("변경할 알림 메시지 본문")
                ));

        this.mockMvc.perform(확정_취소_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }
}
