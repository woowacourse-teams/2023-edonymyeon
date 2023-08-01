package edonymyeon.backend.consumption.application.dto;

import java.util.List;

public record RecentConsumptionsResponse(
        String startMonth,
        String endMonth,
        List<ConsumptionPriceResponse> consumptions
) {

}
