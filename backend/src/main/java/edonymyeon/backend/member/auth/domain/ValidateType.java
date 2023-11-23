package edonymyeon.backend.member.auth.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.Arrays;

public enum ValidateType {
    EMAIL, NICKNAME;

    public static ValidateType from(String target) {
        return Arrays.stream(values())
                .filter(validateType -> validateType.name().equals(target.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.ILLEGAL_ARGUMENT_TYPE));
    }
}
