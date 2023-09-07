package edonymyeon.backend.report.application;

public record ReportRequest(
        String type,
        Long referenceId,
        int reportCategoryId,
        String content) {
}
