package edonymyeon.backend.auth.docs;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.KakaoAuthResponseProvider;
import edonymyeon.backend.member.application.dto.response.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.support.DocsTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("NonAsciiCharacters")
class AuthControllerDocsTest extends DocsTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private KakaoAuthResponseProvider kakaoAuthResponseProvider;

    public AuthControllerDocsTest(final MockMvc mockMvc,
                                  final ObjectMapper objectMapper) {
        super(mockMvc, objectMapper);
    }

    @Test
    void 로그인_문서화() throws Exception {
        final String email = "example@example.com";
        final String password = "password1234!";
        final String deviceToken = "deviceToken";

        final LoginRequest request = new LoginRequest(email, password, deviceToken);

        when(authService.login(request)).thenReturn(new ActiveMemberId(1L));

        final MockHttpServletRequestBuilder 로그인_요청 = post("/login")
                .header("X-API-VERSION", 1, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        final FieldDescriptor[] 로그인_요청_파라미터 = {
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호"),
                fieldWithPath("deviceToken").description("로그인시 사용한 디바이스 토큰")
        };

        final RestDocumentationResultHandler 문서화 = document("login",
                preprocessRequest(prettyPrint()),
                requestFields(로그인_요청_파라미터)
        );

        mockMvc.perform(로그인_요청)
                .andExpect(status().isOk())
                .andDo(문서화);
    }

    @Test
    void 카카오_로그인_문서화() throws Exception {
        final KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("accessToken!!!!!", "deviceToken");
        final KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse(1L);
        final ActiveMemberId activeMemberId = new ActiveMemberId(1L);

        when(kakaoAuthResponseProvider.request(kakaoLoginRequest)).thenReturn(kakaoLoginResponse);
        when(authService.loginByKakao(kakaoLoginResponse, kakaoLoginRequest.deviceToken())).thenReturn(activeMemberId);

        final MockHttpServletRequestBuilder 로그인_요청 = post("/auth/kakao/login")
                .header("X-API-VERSION", 1, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kakaoLoginRequest));

        final FieldDescriptor[] 로그인_요청_파라미터 = {
                fieldWithPath("accessToken").description("카카오 액세스 토큰"),
                fieldWithPath("deviceToken").description("로그인 시 사용한 디바이스의 식별자")
        };

        final RestDocumentationResultHandler 문서화 = document("kakao-login",
                preprocessRequest(prettyPrint()),
                requestFields(로그인_요청_파라미터)
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
                .header("X-API-VERSION", 1, 2)
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
        final JoinRequest request = new JoinRequest("email@email.com", "password1234!", "testNickname",
                "kj234jkn342kj");

        final MockHttpServletRequestBuilder 회원가입_요청 = post("/join")
                .header("X-API-VERSION", 1, 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        FieldDescriptor[] 요청값 = {
                fieldWithPath("email").description("사용할 이메일"),
                fieldWithPath("password").description("사용할 비밀번호"),
                fieldWithPath("nickname").description("사용할 닉네임"),
                fieldWithPath("deviceToken").description("회원가입시 사용한 디바이스 토큰")
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
