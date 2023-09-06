package edonymyeon.backend.report.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import edonymyeon.backend.global.exception.EdonymyeonException;

import static edonymyeon.backend.global.exception.ExceptionInformation.REPORT_TYPE_NOT_FOUND;

public enum ReportType {
    POST, COMMENT;

    @JsonCreator
    public static ReportType from(String type) {
        try {
            return ReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EdonymyeonException(REPORT_TYPE_NOT_FOUND);
        }
    }
}
