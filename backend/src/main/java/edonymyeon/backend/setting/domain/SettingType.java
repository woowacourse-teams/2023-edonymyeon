package edonymyeon.backend.setting.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class SettingType {
    public static SettingType NOTIFICATION_PER_THUMBS
            = new SettingType("1003", "좋아요/싫어요 건별 알림 수신", Category.THUMB, Weight.FIVE);
    public static SettingType NOTIFICATION_PER_10_THUMBS
            = new SettingType("1002", "좋아요/싫어요 10개당 알림 수신", Category.THUMB, Weight.FIVE);
    public static SettingType NOTIFICATION_THUMBS
            = new SettingType("1001", "좋아요 싫어요 알림 수신", Category.THUMB, Weight.TEN);

    public static SettingType NOTIFICATION_PER_COMMENT
            = new SettingType("2001", "자신의 글에 댓글을 남겼을 때 알림 수신", Category.COMMENT, Weight.FIVE);
    public static SettingType NOTIFICATION_CONSUMPTION_CONFIRMATION_REMINDING
            = new SettingType("5001", "특정 시간마다 소비 확정해야 한다는 알림 수신", Category.COMMENT, Weight.FIVE);

    public static SettingType NOTIFICATION
            = new SettingType("0001", "푸시 알림 수신", Category.ALL, Weight.TEN);

    private String serialNumber;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private int weight;

    private SettingType(final String serialNumber, final String name, final Category category, final Weight weight) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.category = category;
        this.weight = weight.getValue();
    }

    public boolean isSerialNumber(final String serialNumber) {
        return Objects.equals(this.serialNumber, serialNumber);
    }

    public boolean isSameCategoryWith(final SettingType settingType) {
        return Objects.equals(this.category, settingType.category);
    }

    public boolean hasLowerWeightThan(final SettingType settingType) {
        return this.weight < settingType.weight;
    }

    public boolean hasHighestWeight() {
        return Objects.equals(this.weight, Weight.TEN.getValue());
    }

    public boolean hasSameWeight(final SettingType settingType) {
        return Objects.equals(this.weight, settingType.weight);
    }
}
