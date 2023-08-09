package edonymyeon.backend.report.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.report.application.ReportRequest;
import edonymyeon.backend.report.application.ReportService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report")
    public ResponseEntity<Void> report(@RequestBody ReportRequest reportRequest, @AuthPrincipal MemberId reporterId) {
        final Long reportId = reportService.report(reportRequest, reporterId);
        return ResponseEntity
                .created(URI.create("/report/" + reportId))
                .build();
    }
}
