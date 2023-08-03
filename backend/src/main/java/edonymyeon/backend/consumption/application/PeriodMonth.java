package edonymyeon.backend.consumption.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_PERIOD_MONTH_ILLEGAL;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PeriodMonth {

    ONE_MONTH_PERIOD(1),
    SIX_MONTH_PERIOD(6);

    private final int periodMonth;

    public static void checkIllegalPeriodMonth(final int periodMonth) {
        if (isIllegal(periodMonth)) {
            throw new EdonymyeonException(CONSUMPTION_PERIOD_MONTH_ILLEGAL);
        }
    }

    private static boolean isIllegal(final int periodMonth) {
        return Arrays.stream(values())
                .noneMatch(value -> value.periodMonth == periodMonth);
    }
}
