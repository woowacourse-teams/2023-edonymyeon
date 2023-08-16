package edonymyeon.backend.preference.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PreferenceKey {

    NOTIFICATION_PER_THUMB(List.of(), "좋아요/싫어요 1건당 알림"),
    NOTIFICATION_PER_10(List.of(NOTIFICATION_PER_THUMB), "좋아요/싫어요 10건당 알림"),
    NOTIFICATION_THUMB(List.of(NOTIFICATION_PER_THUMB, NOTIFICATION_PER_10), "좋아요/싫어요 전체 알림"),
    ;

    private final List<PreferenceKey> dependentPreferences;
    private final String description;

    public boolean isDependentBy(PreferenceKey preferenceKey) {
        return preferenceKey.dependentPreferences.contains(this);
    }
}
