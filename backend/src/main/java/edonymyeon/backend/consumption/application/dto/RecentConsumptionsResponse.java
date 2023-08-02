package edonymyeon.backend.consumption.application.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record RecentConsumptionsResponse(
        String startMonth,
        String endMonth,
        List<ConsumptionPriceResponse> consumptions
) {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

    public static RecentConsumptionsResponse of(
            final LocalDate startDate,
            final LocalDate currentDate,
            final List<ConsumptionPriceResponse> consumptionPriceResponses
    ) {
        final String startMonth = startDate.format(formatter);
        final String endMonth = currentDate.format(formatter);
        return new RecentConsumptionsResponse(startMonth, endMonth, consumptionPriceResponses);
    }
}
