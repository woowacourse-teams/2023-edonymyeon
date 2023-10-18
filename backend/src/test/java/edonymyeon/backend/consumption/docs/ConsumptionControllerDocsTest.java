package edonymyeon.backend.consumption.docs;

import static edonymyeon.backend.auth.ui.SessionConst.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.consumption.application.ConsumptionService;
import edonymyeon.backend.consumption.application.dto.ConsumptionPriceResponse;
import edonymyeon.backend.consumption.application.dto.RecentConsumptionsResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.support.DocsTest;
import edonymyeon.backend.support.TestMemberBuilder;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
class ConsumptionControllerDocsTest extends DocsTest {

    private final TestMemberBuilder testMemberBuilder = new TestMemberBuilder(null);

    @MockBean
    private ConsumptionService consumptionService;

    @MockBean
    private MemberRepository memberRepository;

    public ConsumptionControllerDocsTest(final MockMvc mockMvc,
                                         final ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    private void 회원_레포지토리를_모킹한다(final Member 회원) {
        when(memberRepository.findByEmail(회원.getEmail())).thenReturn(Optional.of(회원));
    }

    private void 소비_서비스를_모킹한다(final RecentConsumptionsResponse response) {
        when(consumptionService.findRecentConsumptions(any(), any())).thenReturn(response);
    }

    @Test
    void 특정기간의_소비금액을_확인한다() throws Exception {
        final Member 회원 = testMemberBuilder.builder()
                .id(1L)
                .buildWithoutSaving();
        final RecentConsumptionsResponse response = new RecentConsumptionsResponse(
                "2023-08",
                "2023-08",
                List.of(new ConsumptionPriceResponse(
                        15000L,
                        33200L
                ))
        );

        회원_레포지토리를_모킹한다(회원);
        소비_서비스를_모킹한다(response);

        final MockHttpServletRequestBuilder 최근_소비_조회_요청 = get("/consumptions?period-month={periodMonth}", 1)
                .sessionAttr(USER.getSessionId(), 회원.getId())
                .header("X-API-VERSION", 1, 2);

        final RestDocumentationResultHandler 문서화 = document("recent-consumptions",
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("period-month").description("조회 기간 ex) 1, 6")
                ),
                responseFields(
                        fieldWithPath("startMonth").description("조회 기간의 첫 달"),
                        fieldWithPath("endMonth").description("조회 기간의 마지막(= 최근, 현재) 달"),
                        fieldWithPath("consumptions[].purchasePrice").description("해당 달의 구매 금액"),
                        fieldWithPath("consumptions[].savingPrice").description("해당 달의 절약 금액")
                )
        );

        mockMvc.perform(최근_소비_조회_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }
}
