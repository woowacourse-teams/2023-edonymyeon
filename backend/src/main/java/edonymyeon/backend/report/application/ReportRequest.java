package edonymyeon.backend.report.application;

public record ReportRequest(Long postId, int reportId, String content) {
}
