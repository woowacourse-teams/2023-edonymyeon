package edonymyeon.backend.report.application;

public record ReportRequest(Long postId, int abusingType, String additionalComment) {
}
