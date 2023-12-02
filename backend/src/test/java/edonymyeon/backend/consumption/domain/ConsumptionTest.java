package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
class ConsumptionTest {

    @ParameterizedTest
    @ValueSource(longs = {0L, 10_000_000_000L})
    void 가격은_0원_이상_100억원_이하여야_한다(final long price) {
        assertDoesNotThrow(() -> Consumption.of(null, PURCHASE, price, 2023, 7));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 10_000_000_001L})
    void 가격이_0원_이상_100억원_이하가_아니면_예외가_발생한다(final long price) {
        assertThatThrownBy(() -> Consumption.of(null, PURCHASE, price, 2023, 7))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(ExceptionInformation.CONSUMPTION_PRICE_ILLEGAL_SIZE.getMessage());
    }

    @Test
    void 년도는_2000년_이상이어야_한다() {
        assertDoesNotThrow(() -> Consumption.of(null, PURCHASE, 1_000L, 2000, 7));
    }

    @Test
    void 년도가_2000년_미만이면_예외가_발생한다() {
        assertThatThrownBy(() -> Consumption.of(null, PURCHASE, 1_000L, 1999, 7))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(ExceptionInformation.CONSUMPTION_YEAR_ILLEGAL.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void 달은_1월부터_12월까지다(final int month) {
        assertDoesNotThrow(() -> Consumption.of(null, PURCHASE, 1_000L, 2000, month));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 13})
    void 유효하지_않은_달이_들어오면_예외가_발생한다(final int month) {
        assertThatThrownBy(() -> Consumption.of(null, PURCHASE, 1_000L, 2000, month))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(ExceptionInformation.CONSUMPTION_MONTH_ILLEGAL.getMessage());
    }

    @Test
    void 미래_시각이_들어오면_예외가_발생한다() {
        final LocalDate currentDate = LocalDate.now();
        final Month month = currentDate.getMonth().plus(1);
        final int year = (month.equals(Month.JANUARY)) ? currentDate.getYear() + 1 : currentDate.getYear();

        assertThatThrownBy(() -> Consumption.of(null, PURCHASE, 1_000L, year, month.getValue()))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(ExceptionInformation.CONSUMPTION_YEAR_MONTH_ILLEGAL.getMessage());
    }
}
