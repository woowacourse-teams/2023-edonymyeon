package edonymyeon.backend.setting.domain;

import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @Enumerated(EnumType.STRING)
    private SettingType settingType;

    @ManyToMany
    @JoinTable(name = "settings_dependent_settings",
            joinColumns = @JoinColumn(name = "setting_id"),
            inverseJoinColumns = @JoinColumn(name = "dependent_setting_id")
    )
    private List<Setting> dependentSettings;

    private EnableStatus enabled;

    public Setting(final Member member, final SettingType settingType,
                   final List<Setting> dependentSettings, final EnableStatus enabled) {
        this.member = member;
        this.settingType = settingType;
        this.dependentSettings = dependentSettings;
        this.enabled = enabled;
    }

    public static List<Setting> makeInitializedSettings(Member member) {
        List<Setting> settings = new ArrayList<>();
        for (SettingType settingType : SettingType.values()) {
            settings.add(new Setting(member, settingType, new ArrayList<>(), EnableStatus.DISABLED));
        }

        for (Setting setting : settings) {
            final List<Setting> dependentSettings = settings.stream()
                    .filter(set -> set.isDependentBy(setting))
                    .toList();
            setting.dependentSettings.addAll(dependentSettings);
        }

        return settings;
    }

    private boolean isDependentBy(final Setting setting) {
        return this.settingType.isDependentBy(setting.settingType);
    }

    public void toggleEnable() {
        this.enabled = this.enabled.toggle();
        handleDependentSettings();
    }

    private void handleDependentSettings() {
        if (this.enabled.isEnabled()) {
            for (Setting dependentSetting : dependentSettings) {
                dependentSetting.releaseForceShut();
            }
        }

        if (!this.enabled.isEnabled()) {
            for (Setting dependentSetting : dependentSettings) {
                dependentSetting.forceShut();
            }
        }
    }

    private void releaseForceShut() {
        this.enabled = this.enabled.releaseForceDisable();
    }

    private void forceShut() {
        this.enabled = this.enabled.forceDisable();
    }

    public boolean isEnabled() {
        return this.enabled.isEnabled();
    }
}
