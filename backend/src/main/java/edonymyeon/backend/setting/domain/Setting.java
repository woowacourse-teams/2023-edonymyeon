package edonymyeon.backend.setting.domain;

import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private SettingType settingType;

    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Setting(final SettingType settingType, final Member member) {
        this.settingType = settingType;
        this.member = member;
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isSameCategoryWith(final Setting setting) {
        return settingType.isSameCategoryWith(setting.settingType);
    }

    public boolean hasLowerWeightThan(final Setting setting) {
        return settingType.hasLowerWeightThan(setting.settingType);
    }

    public boolean hasHighestWeight() {
        return settingType.hasHighestWeight();
    }

    public boolean hasSameWeight(final Setting setting) {
        return settingType.hasSameWeight(setting.settingType);
    }

    public boolean isPrimary() {
        return settingType.isPrimary();
    }

    public String getSerialNumber() {
        return settingType.getSerialNumber();
    }
}
