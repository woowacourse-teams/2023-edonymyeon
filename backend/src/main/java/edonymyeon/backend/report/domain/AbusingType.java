package edonymyeon.backend.report.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.Arrays;

public enum AbusingType {
    DDONG(4);

    private final int typeCode;

    AbusingType(final int typeCode) {
        this.typeCode = typeCode;
    }

    public static AbusingType of(final int abusingType) {
        return Arrays.stream(AbusingType.values())
                .filter(type -> type.typeCode == abusingType)
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.ABUSING_TYPE_NOT_FOUND));
    }
}
