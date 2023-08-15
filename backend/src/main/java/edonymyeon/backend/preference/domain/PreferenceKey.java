package edonymyeon.backend.preference.domain;

public enum PreferenceKey {

    NOTIFICATION_PER_THUMB(PreferenceCategory.NOTIFICATION, "좋아요/싫어요 1건당 알림"),
    NOTIFICATION_PER_10(PreferenceCategory.NOTIFICATION, "좋아요/싫어요 10건당 알림"),
    NOTIFICATION_THUMB(PreferenceCategory.NOTIFICATION, "좋아요/싫어요 전체 알림"),
    ;

    private final PreferenceCategory preferenceCategory;

    private final String description;

    PreferenceKey(final PreferenceCategory preferenceCategory, final String description) {
        this.preferenceCategory = preferenceCategory;
        this.description = description;
    }
}
