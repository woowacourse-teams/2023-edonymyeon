package edonymyeon.backend.setting.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SettingType {

    //TODO: A가 켜지면 B는 꺼지는 경우도 고려해야 한다
    NOTIFICATION_PER_THUMB(List.of(), "좋아요/싫어요 1건당 알림"),
    NOTIFICATION_PER_10(List.of(NOTIFICATION_PER_THUMB), "좋아요/싫어요 10건당 알림"),
    NOTIFICATION_THUMB(List.of(NOTIFICATION_PER_THUMB, NOTIFICATION_PER_10), "좋아요/싫어요 전체 알림"),
    NOTIFICATION(List.of(NOTIFICATION_THUMB, NOTIFICATION_PER_10, NOTIFICATION_PER_THUMB), "전체 알림"),
    ;

    private final List<SettingType> dependentSettings;
    private final String description;

    public boolean isDependentBy(SettingType settingType) {
        return settingType.dependentSettings.contains(this);
    }
}
