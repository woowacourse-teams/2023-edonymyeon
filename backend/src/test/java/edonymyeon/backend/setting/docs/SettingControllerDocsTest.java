package edonymyeon.backend.setting.docs;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.application.dto.SettingRequest;
import edonymyeon.backend.setting.application.dto.SettingResponse;
import edonymyeon.backend.setting.application.dto.SettingsResponse;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.DocsTest;
import edonymyeon.backend.support.TestMemberBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
public class SettingControllerDocsTest extends DocsTest {

    @Autowired
    private TestMemberBuilder testMemberBuilder;

    @MockBean
    private SettingService settingService;

    public SettingControllerDocsTest(final MockMvc mockMvc,
                                     final ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    @Test
    void 선택가능한_설정_목록을_본다() throws Exception {
        final Member 회원 = testMemberBuilder.builder()
                .build();

        when(settingService.findSettingsByMemberId(new ActiveMemberId(회원.getId()))).thenReturn(new SettingsResponse(
                List.of(
                        new SettingResponse(SettingType.NOTIFICATION.getSerialNumber(), false),
                        new SettingResponse(
                                SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING.getSerialNumber(), false),
                        new SettingResponse(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(), false),
                        new SettingResponse(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), false),
                        new SettingResponse(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), false)
                )
        ));

        final MockHttpServletRequestBuilder 설정_조회_요청 = get("/preference/notification")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .sessionAttr(USER.getSessionId(), 회원.getId());

        final RestDocumentationResultHandler 문서화 = document("notification-findAll",
                preprocessRequest(prettyPrint()),
                responseFields(
                        fieldWithPath("notifications").description("사용자가 수정 가능한 설정의 리스트"),
                        fieldWithPath("notifications[].preferenceType").description("설정의 고유 식별자"),
                        fieldWithPath("notifications[].enabled").description("설정의 활성화 여부")
                )
        );

        this.mockMvc.perform(설정_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 사용자가_설정_하나를_활성화_또는_비활성화한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder()
                .build();

        when(settingService.findSettingsByMemberId(new ActiveMemberId(회원.getId()))).thenReturn(new SettingsResponse(
                List.of(
                        new SettingResponse(SettingType.NOTIFICATION.getSerialNumber(), true),
                        new SettingResponse(
                                SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING.getSerialNumber(), false),
                        new SettingResponse(SettingType.NOTIFICATION_PER_COMMENT.getSerialNumber(), false),
                        new SettingResponse(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), true),
                        new SettingResponse(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), false)
                )
        ));

        final MockHttpServletRequestBuilder 설정_조회_요청 = post("/preference/notification")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .sessionAttr(USER.getSessionId(), 회원.getId())
                .content(objectMapper.writeValueAsString(
                        new SettingRequest(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber())));

        final RestDocumentationResultHandler 문서화 = document("notification-toggle",
                preprocessRequest(prettyPrint()),
                requestFields(
                        fieldWithPath("preferenceType").description("활성화 또는 비활성화한 설정의 고유 식별자")
                ),
                responseFields(
                        fieldWithPath("notifications").description("사용자가 수정 가능한 설정의 리스트"),
                        fieldWithPath("notifications[].preferenceType").description("설정의 고유 식별자"),
                        fieldWithPath("notifications[].enabled").description("설정의 활성화 여부")
                )
        );

        this.mockMvc.perform(설정_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }
}
