package edonymyeon.backend.consumption.domain;

import java.util.Objects;

public enum ConsumptionType {

    PURCHASE,
    SAVING;

    public static ConsumptionType classifyConsumptionType(final Long purchasePrice) {
        //todo: 구매를 했는데 0원으로 구매했다면?? 0원 절약 vs 0원 구매 -> 일단 0원 구매로 분류
        if (Objects.isNull(purchasePrice)) {
            return SAVING;
        }
        return PURCHASE;
    }
}
