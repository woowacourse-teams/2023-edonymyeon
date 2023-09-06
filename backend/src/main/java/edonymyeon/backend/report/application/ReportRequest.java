package edonymyeon.backend.report.application;

import edonymyeon.backend.report.domain.ReportType;

public record ReportRequest(
        ReportType type,
        Long referenceId,
        int reportCategoryId,
        String content) {
}
