package edonymyeon.backend.member.application.dto.response;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;

public record ConsumptionResponse(
        String type,
        Long purchasePrice,
        Integer year,
        Integer month) {

    public static ConsumptionResponse of(Consumption consumption) {
        return new ConsumptionResponse(
                consumption.getConsumptionType().name(),
                consumption.getPrice(),
                consumption.getConsumptionYear(),
                consumption.getConsumptionYear()
        );
    }

    public static ConsumptionResponse none() {
        return new ConsumptionResponse(
                ConsumptionType.NONE.name(),
                0L,
                0,
                0
        );
    }
}
