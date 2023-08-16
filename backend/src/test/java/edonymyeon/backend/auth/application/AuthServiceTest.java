package edonymyeon.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.preference.application.PreferenceService;
import edonymyeon.backend.support.IntegrationTest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@RequiredArgsConstructor
@IntegrationTest
class AuthServiceTest {

    private final AuthService authService;

    @SpyBean
    private PreferenceService preferenceService;

    @Test
    void 회원가입시_사용한_디바이스_정보를_함께_저장한다(@Autowired EntityManager entityManager) {
        doNothing().when(preferenceService).initializeMemberPreference(any());

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
        doNothing().when(preferenceService).initializeMemberPreference(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        verify(preferenceService, atLeastOnce()).initializeMemberPreference(any());
    }
}
