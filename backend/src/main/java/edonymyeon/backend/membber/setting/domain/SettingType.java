package edonymyeon.backend.membber.setting.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Arrays;
import java.util.Objects;

public enum SettingType {
    /**
     * 좋아요/싫어요 건별 알림 수신
     */
    NOTIFICATION_PER_THUMBS("1003", SettingTypeCategory.THUMB, Weight.FIVE),
    /**
     * 좋아요/싫어요 10개당 알림 수신
     */
    NOTIFICATION_PER_10_THUMBS("1002", SettingTypeCategory.THUMB, Weight.FIVE),
    /**
     * 자신의 글에 댓글을 남겼을 때 알림 수신
     */
    NOTIFICATION_PER_COMMENT("2001", SettingTypeCategory.COMMENT, Weight.FIVE),
    /**
     * 특정 시간마다 소비 확정해야 한다는 알림 수신
     */
    NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING("5001", SettingTypeCategory.REMINDING, Weight.FIVE),
    /**
     * 푸시 알림 수신
     */
    NOTIFICATION("0001", SettingTypeCategory.ALL, Weight.TEN);

    private final String serialNumber;

    @Enumerated(EnumType.STRING)
    private final SettingTypeCategory category;

    private final int weight;

    SettingType(final String serialNumber, final SettingTypeCategory category, final Weight weight) {
        this.serialNumber = serialNumber;
        this.category = category;
        this.weight = weight.getValue();
    }

    public static SettingType from(final String settingSerialNumber) {
        return Arrays.stream(values())
                .filter(settingType -> Objects.equals(settingType.serialNumber, settingSerialNumber))
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.MEMBER_SETTING_SERIAL_NOT_FOUNT));
    }

    public boolean isSameCategoryWith(final SettingType settingType) {
        return Objects.equals(this.category, settingType.category);
    }

    public boolean hasLowerWeightThan(final SettingType settingType) {
        return this.weight < settingType.weight;
    }

    public boolean hasSameWeight(final SettingType settingType) {
        return Objects.equals(this.weight, settingType.weight);
    }

    public boolean isPrimary() {
        return Objects.equals(this.category, SettingTypeCategory.ALL);
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
