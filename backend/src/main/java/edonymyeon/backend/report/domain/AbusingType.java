package edonymyeon.backend.report.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.Arrays;

public enum AbusingType {
    COMMERCIAL_PURPOSE(1),
    PERSONAL_INFORMATION_DISCLOSURE(2),
    OBSCENITY(3),
    PROFANITY(4),
    REPETITIVE_CONTENT(5),
    ETC(6);

    private final int typeCode;

    AbusingType(final int typeCode) {
        this.typeCode = typeCode;
    }

    public static AbusingType of(final int abusingType) {
        return Arrays.stream(values())
                .filter(type -> type.typeCode == abusingType)
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.ABUSING_TYPE_NOT_FOUND));
    }
}
