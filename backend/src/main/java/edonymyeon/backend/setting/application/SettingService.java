package edonymyeon.backend.setting.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.setting.application.activationmanager.ActivationManager;
import edonymyeon.backend.setting.application.deactivationmanager.DeactivationManager;
import edonymyeon.backend.setting.application.dto.SettingsResponse;
import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.setting.repository.SettingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SettingService {

    private final SettingRepository settingRepository;

    private final MemberRepository memberRepository;

    private final List<ActivationManager> activationManagers;

    private final List<DeactivationManager> deactivationManagers;

    private static List<Setting> getDefaultSettings(final Member member) {
        return List.of(
                new Setting(SettingType.NOTIFICATION_PER_THUMBS, member),
                new Setting(SettingType.NOTIFICATION_PER_10_THUMBS, member),

                new Setting(SettingType.NOTIFICATION_PER_COMMENT, member),
                new Setting(SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING, member),

                new Setting(SettingType.NOTIFICATION, member)
        );
    }

    public void initializeSettings(final Member member) {
        final List<Setting> defaultSettings = getDefaultSettings(member);
        settingRepository.saveAll(defaultSettings);
    }

    public void toggleSetting(final String settingSerialNumber, final MemberId memberId) {
        final Member member = findMemberById(memberId);
        final Setting setting = settingRepository.findByMemberIdAndSettingType(member.getId(), SettingType.from(settingSerialNumber));

        if (!setting.isActive()) {
            activateSetting(member, setting);
            return;
        }
        setting.ifActive(() -> deactivateSetting(member, setting));
    }

    private Member findMemberById(final MemberId memberId) {
        return memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.MEMBER_ID_NOT_FOUND));
    }

    private void activateSetting(final Member member, final Setting setting) {
        setting.activate();

        final List<Setting> settings = settingRepository.findByMemberId(member.getId());
        for (ActivationManager activationManager : activationManagers) {
            activationManager.manage(settings, setting);
        }
    }

    private void deactivateSetting(final Member member, final Setting setting) {
        setting.deactivate();

        final List<Setting> settings = settingRepository.findByMemberId(member.getId());
        for (DeactivationManager deactivationManager : deactivationManagers) {
            deactivationManager.manage(settings, setting);
        }
    }

    public SettingsResponse findSettingsByMemberId(final MemberId memberId) {
        final List<Setting> settings = settingRepository.findByMemberId(memberId.id());
        return SettingsResponse.from(settings);
    }

    public boolean isSettingActive(final MemberId memberId, final SettingType settingType) {
        final Setting setting = settingRepository.findByMemberIdAndSettingType(memberId.id(), settingType);
        return setting.isActive();
    }
}
