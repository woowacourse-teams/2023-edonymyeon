package edonymyeon.backend.auth.application;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.preference.application.PreferenceService;
import edonymyeon.backend.support.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;

@RequiredArgsConstructor
@IntegrationTest
class AuthServiceTest {

    private final AuthService authService;

    @SpyBean
    private PreferenceService preferenceService;

    @Test
    void 회원가입_이후_설정초기화_작업을_수행한다() {
        doNothing().when(preferenceService).initializeMemberPreference(any());

        authService.joinMember(new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        verify(preferenceService, atLeastOnce()).initializeMemberPreference(any());
    }
}
