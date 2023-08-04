package edonymyeon.backend.consumption.application.dto;

import edonymyeon.backend.consumption.domain.ConsumptionsPerMonth;

public record ConsumptionPriceResponse(Long purchasePrice, Long savingPrice) {

    public static ConsumptionPriceResponse from(final ConsumptionsPerMonth consumptionsPerMonth) {
        return new ConsumptionPriceResponse(
                consumptionsPerMonth.calculateTotalPurchasePrice(),
                consumptionsPerMonth.calculateTotalSavingPrice()
        );
    }
}
