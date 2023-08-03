package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;
import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static edonymyeon.backend.global.exception.ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTIONS_NULL;
import static edonymyeon.backend.global.exception.ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTIONS_PERIOD_NOT_SAME;

import edonymyeon.backend.global.exception.BusinessLogicException;
import java.util.List;
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
        if (consumptions.isEmpty() || isSamePeriod(consumptions)) {
            return;
        }
        throw new BusinessLogicException(BUSINESS_LOGIC_ERROR_CONSUMPTIONS_PERIOD_NOT_SAME);
    }

    public boolean isSamePeriod(final List<Consumption> consumptions) {
        final Consumption firstConsumption = consumptions.get(0);
        return consumptions.stream()
                .allMatch(consumption -> consumption.isSameYearMonth(firstConsumption));
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
