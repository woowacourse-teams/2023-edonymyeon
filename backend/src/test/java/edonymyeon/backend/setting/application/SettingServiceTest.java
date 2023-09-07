package edonymyeon.backend.setting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.setting.repository.SettingRepository;
import edonymyeon.backend.support.IntegrationFixture;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@RequiredArgsConstructor
@SuppressWarnings("NonAsciiCharacters")
class SettingServiceTest extends IntegrationFixture {

    @SpyBean
    private SettingService settingService;

    private final SettingRepository settingRepository;

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
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        List<Setting> settings = settingRepository.findByMemberId(member.getId());
        assertThat(settings).hasSize(5);

        assertThat(settings.stream()
                .map(Setting::isActive)
                .toList())
                .containsOnly(false);
    }

    @Test
    void 설정을_껐다가_켤_수_있다() {
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), new ActiveMemberId(member.getId()));
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isTrue();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), new ActiveMemberId(member.getId()));
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isFalse();
    }

    @Test
    void 전략에_따라_어떤_알림이_켜지면_어떤_알림은_꺼지기도_한다() {
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), new ActiveMemberId(member.getId()));
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isTrue();
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_THUMBS)
                .isActive()).isFalse();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), new ActiveMemberId(member.getId()));
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isFalse();
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_THUMBS)
                .isActive()).isTrue();
    }

    @Test
    void 동일한_가중치의_설정이_enabled되면_동일한_가중치인_설정은_모두_disabled() {
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(),
                new ActiveMemberId(member.getId()));

        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isTrue();
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_THUMBS)
                .isActive()).isFalse();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(),
                new ActiveMemberId(member.getId()));

        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isFalse();
        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_THUMBS)
                .isActive()).isTrue();
    }

    @Test
    void ALL_type인_설정이_disabled되면_전체_설정_disabled() {
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), new ActiveMemberId(member.getId()));

        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isTrue();

        settingService.toggleSetting(SettingType.NOTIFICATION.getSerialNumber(), new ActiveMemberId(member.getId()));

        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isFalse();
    }

    @Test
    void ALL이_아닌_설정이_enabled_또는_disabled되면_다른_type의_설정에_영향_X() {
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        settingService.toggleSetting(SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), new ActiveMemberId(member.getId()));

        settingService.toggleSetting(SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING.getSerialNumber(), new ActiveMemberId(member.getId()));

        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION_PER_10_THUMBS)
                .isActive()).isTrue();
    }

    @Test
    void 설정이_하나라도_enabled되면_ALL_타입의_설정_enabled() {
        final Member member = 회원을_저장하고_기본설정을_부여한다();

        settingService.toggleSetting(SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING.getSerialNumber(), new ActiveMemberId(member.getId()));

        assertThat(settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.NOTIFICATION)
                .isActive()).isTrue();
    }

    @NotNull
    private Member 회원을_저장하고_기본설정을_부여한다() {
        final Member member = memberTestSupport.builder().build();
        settingService.initializeSettings(member);
        return member;
    }
}
