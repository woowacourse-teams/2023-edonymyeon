package edonymyeon.backend.report.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;

import java.util.Objects;

import static edonymyeon.backend.global.exception.ExceptionInformation.REPORT_TYPE_NOT_FOUND;

public enum ReportType {
    POST, COMMENT;

    public static ReportType from(String type) {
        if(Objects.isNull(type) || type.isBlank()){
            throw new EdonymyeonException(REPORT_TYPE_NOT_FOUND);
        }

        try {
            return ReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EdonymyeonException(REPORT_TYPE_NOT_FOUND);
        }
    }
}
