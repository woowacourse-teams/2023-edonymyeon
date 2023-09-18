package edonymyeon.backend.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.support.IntegrationFixture;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class MemberServiceTest extends IntegrationFixture {

    private final DeviceRepository deviceRepository;
    private final AuthService authService;

    @Test
    void 하나의_디바이스는_하나의_계정에서만_활성화_되어야_한다_회원가입() {
        final String 동일한_디바이스_토큰 = "testDeviceToken";

        authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "nickname", 동일한_디바이스_토큰));
        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰)).isPresent();

        authService.joinMember(new JoinRequest("test2@gmail.com", "password123!", "nickname2", 동일한_디바이스_토큰));
        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰))
                .as("같은 디바이스가 여러 계정에서 활성화되어 있다면 NonUniqueResultException으로 인해 테스트가 실패해야 한다.")
                .isPresent();
    }

    @Test
    void 하나의_디바이스는_하나의_계정에서만_활성화_되어야_한다_로그인() {
        final String 동일한_디바이스_토큰 = "testDeviceToken";
        authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "nickname", 동일한_디바이스_토큰));
        authService.joinMember(new JoinRequest("test2@gmail.com", "password123!", "nickname2", 동일한_디바이스_토큰));

        authService.login(new LoginRequest("test@gmail.com", "password123!", 동일한_디바이스_토큰));
        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰))
                .as("같은 디바이스가 여러 계정에서 활성화되어 있다면 NonUniqueResultException으로 인해 테스트가 실패해야 한다.")
                .isPresent();
    }

    @Test
    void 하나의_디바이스는_하나의_계정에서만_활성화_되어야_한다_소셜로그인() {
        final String 동일한_디바이스_토큰 = "testDeviceToken";
        authService.loginByKakao(new KakaoLoginResponse(1L), 동일한_디바이스_토큰);
        authService.loginByKakao(new KakaoLoginResponse(2L), 동일한_디바이스_토큰);

        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰))
                .as("같은 디바이스가 여러 계정에서 활성화되어 있다면 NonUniqueResultException으로 인해 테스트가 실패해야 한다.")
                .isPresent();
    }
}
