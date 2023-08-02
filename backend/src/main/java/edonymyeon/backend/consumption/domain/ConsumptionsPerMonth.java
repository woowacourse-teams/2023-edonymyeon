package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;
import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static edonymyeon.backend.global.exception.ExceptionInformation.SERVER_ERROR_CONSUMPTIONS_NULL;
import static edonymyeon.backend.global.exception.ExceptionInformation.SERVER_ERROR_CONSUMPTION_MONTH_NOT_SAME;
import static edonymyeon.backend.global.exception.ExceptionInformation.SERVER_ERROR_CONSUMPTION_YEAR_NOT_SAME;

import edonymyeon.backend.global.exception.EdonymyeonException;
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
            throw new EdonymyeonException(SERVER_ERROR_CONSUMPTIONS_NULL);
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
                throw new EdonymyeonException(SERVER_ERROR_CONSUMPTION_YEAR_NOT_SAME);
            }
            if (!Objects.equals(consumption.getConsumptionMonth(), referenceMonth)) {
                throw new EdonymyeonException(SERVER_ERROR_CONSUMPTION_MONTH_NOT_SAME);
            }
        }
    }

    public Long calculateTotalPurchasePrice() {
        long purchasePrice = 0;
        for (Consumption consumption : consumptions) {
            if (consumption.getConsumptionType() == PURCHASE) {
                purchasePrice += consumption.getPrice();
            }
        }
        return purchasePrice;
    }

    public Long calculateTotalSavingPrice() {
        long savingPrice = 0;
        for (Consumption consumption : consumptions) {
            if (consumption.getConsumptionType() == SAVING) {
                savingPrice += consumption.getPrice();
            }
        }
        return savingPrice;
    }
}
