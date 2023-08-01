package edonymyeon.backend.consumption.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.consumption.application.ConsumptionStatisticsService;
import edonymyeon.backend.consumption.application.dto.RecentConsumptionsResponse;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ConsumptionController {

    private final ConsumptionStatisticsService consumptionStatisticsService;

    @GetMapping("/consumptions")
    public ResponseEntity<RecentConsumptionsResponse> findRecentConsumptions(@AuthPrincipal final MemberIdDto memberId,
                                                                             @RequestParam("period-month") final Integer periodMonth) {
        final RecentConsumptionsResponse response = consumptionStatisticsService.findRecentConsumptions(
                memberId, periodMonth);
        return ResponseEntity.ok()
                .body(response);
    }
}
