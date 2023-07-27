package edonymyeon.backend.auth.application;

public enum ValidateType {
    EMAIL, NICKNAME;

    public static ValidateType from(String target) {
        return ValidateType.valueOf(target.toUpperCase());
    }
}
