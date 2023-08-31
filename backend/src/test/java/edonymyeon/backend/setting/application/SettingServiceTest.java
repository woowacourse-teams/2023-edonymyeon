package edonymyeon.backend.setting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.support.IntegrationFixture;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class SettingServiceTest extends IntegrationFixture {

    @SpyBean
    private SettingService settingService;

    @Autowired
    SettingRepository settingRepository;

    @Test
    void 최초_회원가입이_완료된_이후_설정_초기화_작업을_진행한다(
            @Autowired AuthService authService
    ) {
        authService.joinMember(new JoinRequest(
                "test@gmail.com",
                "testPassword123!",
                "nickname",
                "testDeviceToken"
        ));

        verify(settingService, atLeastOnce()).initializeSettings(any());
    }

    @Test
    void 설정을_초기화하면_회원에_대한_기본_설정이_저장되어_있다() {
        final Member member = memberTestSupport.builder().build();
        settingService.initializeSettings(member);

        List<Setting> settings = settingRepository.findByMemberId(member.getId());
        assertThat(settings).hasSize(SettingService.getDefaultSettings(member).size());

        assertThat(settings.stream()
                .map(Setting::isActive)
                .toList())
                .containsOnly(false);
    }

    @Test
    void 가중치_10인_설정이_disable되면_해당_type의_설정_모두_disabled(
            @Autowired EntityManager entityManager
    ) {
        final Member member = memberTestSupport.builder().build();
        settingService.initializeSettings(member);

        settingService.toggleSetting("1001", member);
        settingService.toggleSetting("1002", member);
        entityManager.flush();
        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1002")
                .isActive()).isTrue();

        settingService.toggleSetting("1001", member);
        entityManager.flush();
        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1002")
                .isActive()).isFalse();
    }

    @Test
    void 가중치_10_미만인_모든_설정이_disabled면_가중치_10인_설정도_disabled() {
        final Member member = memberTestSupport.builder().build();
        settingService.initializeSettings(member);

        settingService.toggleSetting("1002", member);

        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1001")
                .isActive()).isTrue();

        settingService.toggleSetting("1002", member);

        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1001")
                .isActive()).isFalse();
    }

    @Test
    void 동일한_가중치의_설정이_enabled되면_동일한_가중치인_설정은_모두_disabled() {
        final Member member = memberTestSupport.builder().build();
        settingService.initializeSettings(member);

        settingService.toggleSetting("1002", member);

        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1002")
                .isActive()).isTrue();
        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1003")
                .isActive()).isFalse();

        settingService.toggleSetting("1003", member);

        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1002")
                .isActive()).isFalse();
        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1003")
                .isActive()).isTrue();
    }

    @Test
    void 십_미만인_설정이_하나라도_enabled되면_가중치_10인_설정_enabled() {
        final Member member = memberTestSupport.builder().build();
        settingService.initializeSettings(member);

        settingService.toggleSetting("1002", member);

        assertThat(settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), "1001")
                .isActive()).isTrue();
    }
}
