package edonymyeon.backend.consumption.domain;

import java.util.Objects;

public enum ConsumptionType {

    PURCHASE,
    SAVING;

    public static ConsumptionType classifyConsumptionType(final Long purchasePrice) {
        if (Objects.isNull(purchasePrice)) {
            return SAVING;
        }
        return PURCHASE;
    }
}
