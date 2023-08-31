package edonymyeon.backend.auth.application;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.application.dto.LogoutRequest;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.support.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@IntegrationTest
class AuthServiceTest {

    @SpyBean
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @Test
    void 로그인_이후_디바이스_교체_작업을_수행한다() {
        doNothing().when(memberService).activateDevice(any(), any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        authService.login(new LoginRequest("test@gmail.com", "@testPassword234", "testToken"));
        verify(memberService, atLeastOnce()).activateDevice(any(), any());
    }

    @Test
    void 소셜로그인_이후에도_디바이스_교체_작업을_수행한다() {
        doNothing().when(memberService).activateDevice(any(), any());

        authService.loginByKakao(new KakaoLoginResponse(1L), "testDeviceToken");

        verify(memberService, atLeastOnce()).activateDevice(any(), any());
    }

    @Test
    void 로그아웃시_인증정보가_담긴_쿠키가_사라진다(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        mockMvc.perform(post("/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Basic ekjnjknnjkn124124")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new LogoutRequest("testDeviceTokenblabla"))))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION));
    }
}
