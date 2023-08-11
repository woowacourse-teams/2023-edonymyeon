package edonymyeon.backend.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edonymyeon.backend.DocsTest;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
public class AuthControllerDocsTest extends DocsTest {

    @MockBean
    private AuthService authService;

    @Test
    void 로그인_문서화() throws Exception {
        final LoginRequest request = new LoginRequest("example@example.com", "password1234!");
        when(authService.findMember(request)).thenReturn(any());

        final MockHttpServletRequestBuilder 로그인_요청 = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        final FieldDescriptor[] 로그인_요청_파라미터 = {
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호")
        };

        final HeaderDescriptor[] 응답_헤더 = {
                headerWithName("Authorization").description("basic Auth 토큰 값")
        };

        final RestDocumentationResultHandler 문서화 = document("login",
                preprocessRequest(prettyPrint()),
                requestFields(로그인_요청_파라미터),
                responseHeaders(응답_헤더)
        );

        mockMvc.perform(로그인_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 회원가입시_중복_확인_문서화() throws Exception {
        String target = "email";
        String value = "example@example.com";
        final DuplicateCheckResponse duplicateCheckResponse = new DuplicateCheckResponse(false);
        when(authService.checkDuplicate(target, value)).thenReturn(duplicateCheckResponse);

        final MockHttpServletRequestBuilder 중복_요청 = get("/join")
                .queryParam("target", target)
                .queryParam("value", value);

        ParameterDescriptor[] 요청_쿼리_파라미터 = {
                parameterWithName("target").description("검증할 타입 ex)email, nickname"),
                parameterWithName("value").description("검증할 값 ex)email@email.com, hoyZzang")
        };

        FieldDescriptor[] 응답값 = {
                fieldWithPath("isUnique").description("해당 요청 값(이메일,닉네임)이 존재하지 않으면 true")
        };

        final RestDocumentationResultHandler 문서화 = document("validate-join",
                preprocessResponse(prettyPrint()),
                queryParameters(요청_쿼리_파라미터),
                responseFields(응답값)
        );

        mockMvc.perform(중복_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 회원가입_문서화() throws Exception {
        final JoinRequest request = new JoinRequest("email@email.com", "password1234!", "testNickname");

        doNothing().when(authService).joinMember(request);

        final MockHttpServletRequestBuilder 회원가입_요청 = post("/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        FieldDescriptor[] 요청값 = {
                fieldWithPath("email").description("사용할 이메일"),
                fieldWithPath("password").description("사용할 비밀번호"),
                fieldWithPath("nickname").description("사용할 닉네임")
        };

        final RestDocumentationResultHandler 문서화 = document("join",
                preprocessRequest(prettyPrint()),
                requestFields(요청값)
        );

        mockMvc.perform(회원가입_요청)
                .andExpect(status().isCreated())
                .andDo(문서화);
    }
}