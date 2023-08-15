package edonymyeon.backend.preference.domain;

public enum PreferenceCategory {
    NOTIFICATION("알림");

    private final String desciption;

    PreferenceCategory(final String desciption) {
        this.desciption = desciption;
    }
}
