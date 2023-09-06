package edonymyeon.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import edonymyeon.backend.auth.application.dto.MemberResponse;
import edonymyeon.backend.auth.domain.PasswordEncoder;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.support.IntegrationTest;
import jakarta.persistence.EntityManager;
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
    private SettingService settingService;

    @SpyBean
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입시_사용한_디바이스_정보를_함께_저장한다(@Autowired EntityManager entityManager) {
        doNothing().when(settingService).initializeSettings(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        final Member member = entityManager.createQuery(
                        "select m from Member m left join fetch m.devices where m.email = :email", Member.class)
                .setParameter("email", "test@gmail.com")
                .getSingleResult();
        assertThat(member.getDevices()).hasSize(1);
    }

    @Test
    void 회원가입_이후_설정초기화_작업을_수행한다() {
        doNothing().when(settingService).initializeSettings(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        verify(settingService, atLeastOnce()).initializeSettings(any());
    }

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

    @Test
    void 기본_회원가입시_DB에_암호화된_비밀번호가_저장된다() {
        final JoinRequest request = new JoinRequest("example@email.com", "password1234!", "nickname", "");
        final Member member = authService.joinMember(request);

        assertSoftly(soft -> {
            soft.assertThat(request.email()).isEqualTo(member.getEmail());
            soft.assertThat(request.password()).isNotEqualTo(member.getPassword());
            verify(passwordEncoder, atLeastOnce()).encode(any());
        });
    }

    @Test
    void 카카오_회원가입시_DB에_암호화된_비밀번호가_저장된다() {
        final SocialInfo socialInfo = SocialInfo.of(SocialInfo.SocialType.KAKAO, 1L);
        authService.joinSocialMember(socialInfo);

        verify(passwordEncoder, atLeastOnce()).encode(any());
    }

    @Test
    void 기본_로그인시_DB에_암호화된_비밀번호와_입력받은_비밀번호를_검증한다() {
        final JoinRequest request = new JoinRequest("example@email.com", "password1234!", "nickname", "");
        authService.joinMember(request);

        assertSoftly(soft -> {
            assertDoesNotThrow(() -> authService.login(request.email(), request.password()));
            verify(passwordEncoder, atLeastOnce()).encode(any());
            verify(passwordEncoder, atLeastOnce()).matches(any(), any());
        });
    }

    @Test
    void 카카오_로그인시_DB에_암호화된_비밀번호와_입력받은_비밀번호를_검증한다() {
        final SocialInfo socialInfo = SocialInfo.of(SocialInfo.SocialType.KAKAO, 1L);
        final Member member = authService.joinSocialMember(socialInfo);

        final KakaoLoginResponse kakaoLoginResponse = new KakaoLoginResponse(socialInfo.getSocialId());
        final MemberResponse memberResponse = authService.loginByKakao(kakaoLoginResponse, "");

        assertSoftly(soft -> {
            soft.assertThat(member.getEmail()).isEqualTo(memberResponse.email());
            soft.assertThat(member.getPassword()).isEqualTo(memberResponse.password());
        });
    }
}
