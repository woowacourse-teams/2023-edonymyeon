package edonymyeon.backend.setting.application;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.setting.domain.Setting;
import edonymyeon.backend.setting.domain.SettingType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public static List<Setting> getDefaultSettings(final Member member) {
        return List.of(
                new Setting(SettingType.NOTIFICATION_PER_THUMBS, member),
                new Setting(SettingType.NOTIFICATION_PER_10_THUMBS, member),
                new Setting(SettingType.NOTIFICATION_THUMBS, member),

                new Setting(SettingType.NOTIFICATION_PER_COMMENT, member),
                new Setting(SettingType.NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING, member),

                new Setting(SettingType.NOTIFICATION, member)
        );
    }

    public void initializeSettings(final Member member) {
        final List<Setting> defaultSettings = getDefaultSettings(member);
        settingRepository.saveAll(defaultSettings);
    }

    public void toggleSetting(final String settingSerialNumber, final Member member) {
        final Setting setting = settingRepository.findByMemberIdAndSettingType_SerialNumber(member.getId(), settingSerialNumber);
        if (!setting.isActive()) {
            activateSetting(member, setting);
            return;
        }

        deactivateSetting(member, setting);
    }

    private void activateSetting(final Member member, final Setting setting) {
        setting.activate();

        final List<Setting> settings = settingRepository.findByMemberId(member.getId());
        deactivateSameWeights(setting, settings);
        activateHighestWeight(setting, settings);
        activatePrimary(settings);
    }

    private static void activatePrimary(final List<Setting> settings) {
        for (Setting set : settings) {
            if (set.isPrimary()) {
                set.activate();
            }
        }
    }

    private static void activateHighestWeight(final Setting setting, final List<Setting> settings) {
        for (Setting set : settings) {
            if (set.isSameCategoryWith(setting) && set.hasHighestWeight()) {
                set.activate();
            }
        }
    }

    private static void deactivateSameWeights(final Setting setting, final List<Setting> settings) {
        for (Setting set : settings) {
            if (!set.equals(setting) && set.isSameCategoryWith(setting) && set.hasSameWeight(setting)) {
                set.deactivate();
            }
        }
    }

    private void deactivateSetting(final Member member, final Setting setting) {
        setting.deactivate();

        final List<Setting> settings = settingRepository.findByMemberId(member.getId());
        deactivateHavingLowerWeight(setting, settings);
        deactivateHighest(setting, settings);
        checkPrimaryDeactivated(setting, settings);
    }

    private static void checkPrimaryDeactivated(final Setting setting, final List<Setting> settings) {
        if (setting.isPrimary()) {
            for (Setting set : settings) {
                set.deactivate();
            }
        }
    }

    private static void deactivateHighest(final Setting setting, final List<Setting> settings) {
        for (Setting set : settings) {
            if (set.isSameCategoryWith(setting) && !set.hasHighestWeight() && set.isActive()) {
                return;
            }
        }

        settings.stream()
                .filter(set -> set.isSameCategoryWith(setting) && set.hasHighestWeight())
                .forEach(Setting::deactivate);
    }

    private static void deactivateHavingLowerWeight(final Setting setting, final List<Setting> settings) {
        for (Setting set : settings) {
            if (set.isSameCategoryWith(setting) && set.hasLowerWeightThan(setting)) {
                set.deactivate();
            }
        }
    }
}
