package edonymyeon.backend.consumption.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.consumption.application.ConsumptionService;
import edonymyeon.backend.consumption.application.dto.RecentConsumptionsResponse;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.application.dto.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    @ApiVersion(value = {1, 2})
    @GetMapping("/consumptions")
    public ResponseEntity<RecentConsumptionsResponse> findRecentConsumptions(@AuthPrincipal final MemberId memberId,
                                                                             @RequestParam("period-month") final Integer periodMonth) {
        final RecentConsumptionsResponse response = consumptionService.findRecentConsumptions(
                memberId, periodMonth);
        return ResponseEntity.ok()
                .body(response);
    }
}
