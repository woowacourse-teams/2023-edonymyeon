package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;
import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static edonymyeon.backend.global.exception.ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTIONS_NULL;
import static edonymyeon.backend.global.exception.ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTION_MONTH_NOT_SAME;
import static edonymyeon.backend.global.exception.ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTION_YEAR_NOT_SAME;

import edonymyeon.backend.global.exception.BusinessLogicException;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ConsumptionsPerMonth {

    private final List<Consumption> consumptions;

    public ConsumptionsPerMonth(final List<Consumption> consumptions) {
        validateNull(consumptions);
        validateSameYearMonth(consumptions);
        this.consumptions = consumptions;
    }

    private void validateNull(final List<Consumption> consumptions) {
        if (consumptions == null) {
            throw new BusinessLogicException(BUSINESS_LOGIC_ERROR_CONSUMPTIONS_NULL);
        }
    }

    private void validateSameYearMonth(final List<Consumption> consumptions) {
        if (consumptions.isEmpty()) {
            return;
        }
        final Consumption referenceConsumption = consumptions.get(0);
        final Integer referenceYear = referenceConsumption.getConsumptionYear();
        final Integer referenceMonth = referenceConsumption.getConsumptionMonth();

        for (Consumption consumption : consumptions) {
            if (!Objects.equals(consumption.getConsumptionYear(), referenceYear)) {
                throw new BusinessLogicException(BUSINESS_LOGIC_ERROR_CONSUMPTION_YEAR_NOT_SAME);
            }
            if (!Objects.equals(consumption.getConsumptionMonth(), referenceMonth)) {
                throw new BusinessLogicException(BUSINESS_LOGIC_ERROR_CONSUMPTION_MONTH_NOT_SAME);
            }
        }
    }

    public Long calculateTotalPurchasePrice() {
        return this.consumptions
                .stream().filter(consumption -> consumption.isType(PURCHASE))
                .mapToLong(Consumption::getPrice)
                .sum();
    }

    public Long calculateTotalSavingPrice() {
        return this.consumptions
                .stream().filter(consumption -> consumption.isType(SAVING))
                .mapToLong(Consumption::getPrice)
                .sum();
    }
}
