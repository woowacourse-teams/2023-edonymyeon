package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;
import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ConsumptionsPerMonth {

    private final List<Consumption> consumptions;

    private ConsumptionsPerMonth(final List<Consumption> consumptions) {
        this.consumptions = consumptions;
    }

    public static List<ConsumptionsPerMonth> of(
            final List<Consumption> consumptions,
            final LocalDate startMonth,
            final LocalDate lastMonth
    ) {
        List<ConsumptionsPerMonth> consumptionsOfPeriod = new ArrayList<>();

        //startMonth -> lastMonth 로 가면서 각 달의 구매/절약 금액을 저장한다.
        LocalDate orderMonth = startMonth;
        while (orderMonth.isBefore(lastMonth) || orderMonth.isEqual(lastMonth)) {
            final LocalDate compareMonth = orderMonth;
            final List<Consumption> consumptionsPerMonth = findConsumptionsOfSameMonth(consumptions, compareMonth);
            consumptionsOfPeriod.add(new ConsumptionsPerMonth(consumptionsPerMonth));
            orderMonth = orderMonth.plusMonths(1);
        }
        return consumptionsOfPeriod;
    }

    private static List<Consumption> findConsumptionsOfSameMonth(final List<Consumption> consumptions,
                                                                 final LocalDate compareMonth) {
        return consumptions.stream()
                .filter(consumption -> consumption.isSameYearMonth(compareMonth))
                .toList();
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
