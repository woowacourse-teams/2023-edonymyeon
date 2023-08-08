package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_MONTH_ILLEGAL;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_PRICE_ILLEGAL_SIZE;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_YEAR_ILLEGAL;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_YEAR_MONTH_ILLEGAL;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@EqualsAndHashCode(of = {"id"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Consumption {

    private static final long MAX_PRICE = 10_000_000_000L;
    private static final int MIN_PRICE = 0;
    private static final int MIN_YEAR = 2000;
    private static final int MAX_MONTH = 12;
    private static final int MIN_MONTH = 1;
    private static final int DEFAULT_DAY = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE) // post가 삭제될때 연관된 소비내역도 삭제될 수 있도록 한다.
    private Post post;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ConsumptionType consumptionType;

    @Column(nullable = false)
    private Long price;

    /**
     * 현재 정책상으로는 yyyy-MM-dd에서 dd는 01로 고정되어 저장됩니다.
     **/
    @Column(nullable = false)
    private LocalDate consumptionDate;

    private Consumption(
            final Post post,
            final ConsumptionType consumptionType,
            final Long price,
            final LocalDate consumptionDate
    ) {
        validate(price, consumptionDate);
        this.post = post;
        this.consumptionType = consumptionType;
        this.price = price;
        this.consumptionDate = consumptionDate;
    }

    public static Consumption of(
            final Post post,
            final ConsumptionType consumptionType,
            final Long purchasePrice,
            final Integer year,
            final Integer month
    ) {
        validateConsumptionYear(year);
        validateConsumptionMonth(month);
        LocalDate consumptionDate = LocalDate.of(year, month, DEFAULT_DAY);
        Long price = purchasePrice;
        if (consumptionType == SAVING) {
            price = post.getPrice();
        }
        return new Consumption(post, consumptionType, price, consumptionDate);
    }

    private void validate(final Long price, final LocalDate consumptionDate) {
        validatePrice(price);
        validateConsumptionYearMonth(consumptionDate);
    }

    private void validatePrice(final Long price) {
        if (Objects.isNull(price) || price < MIN_PRICE || price > MAX_PRICE) {
            throw new EdonymyeonException(CONSUMPTION_PRICE_ILLEGAL_SIZE);
        }
    }

    private static void validateConsumptionYear(final Integer consumptionYear) {
        if (Objects.isNull(consumptionYear) || consumptionYear < MIN_YEAR) {
            throw new EdonymyeonException(CONSUMPTION_YEAR_ILLEGAL);
        }
    }

    private static void validateConsumptionMonth(final Integer consumptionMonth) {
        if (Objects.isNull(consumptionMonth) || consumptionMonth < MIN_MONTH || consumptionMonth > MAX_MONTH) {
            throw new EdonymyeonException(CONSUMPTION_MONTH_ILLEGAL);
        }
    }

    private void validateConsumptionYearMonth(final LocalDate consumptionDate) {
        LocalDate currentDate = LocalDate.now();
        if (consumptionDate.isAfter(currentDate)) {
            throw new EdonymyeonException(CONSUMPTION_YEAR_MONTH_ILLEGAL);
        }
    }

    boolean isType(final ConsumptionType consumptionType) {
        return this.consumptionType == consumptionType;
    }

    private boolean isSameYear(final LocalDate consumptionDate) {
        return this.consumptionDate.getYear() == consumptionDate.getYear();
    }

    private boolean isSameMonth(final LocalDate consumptionDate) {
        return this.consumptionDate.getMonth() == consumptionDate.getMonth();
    }

    public boolean isSameYearMonth(final LocalDate consumptionDate) {
        return isSameYear(consumptionDate) && isSameMonth(consumptionDate);
    }

    public int getConsumptionYear() {
        return consumptionDate.getYear();
    }

    public int getConsumptionMonth() {
        return consumptionDate.getMonth().getValue();
    }
}
