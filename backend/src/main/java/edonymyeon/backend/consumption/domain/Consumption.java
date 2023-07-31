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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Consumption {

    private static final long MAX_PRICE = 10_000_000_000L;
    private static final int MIN_PRICE = 0;
    private static final int MIN_YEAR = 0;
    private static final int MAX_MONTH = 12;
    private static final int MIN_MONTH = 1;

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

    @Column(nullable = false)
    private Integer consumptionYear;

    @Column(nullable = false)
    private Integer consumptionMonth;

    private Consumption(
            final Post post,
            final ConsumptionType consumptionType,
            final Long price,
            final int consumptionYear,
            final int consumptionMonth
    ) {
        validate(price, consumptionYear, consumptionMonth);
        this.post = post;
        this.consumptionType = consumptionType;
        this.price = price;
        this.consumptionYear = consumptionYear;
        this.consumptionMonth = consumptionMonth;
    }

    public static Consumption of(
            final Post post,
            final Long purchasePrice,
            final Integer year,
            final Integer month
    ) {
        ConsumptionType consumptionType = ConsumptionType.classifyConsumptionType(purchasePrice);
        Long price = purchasePrice;
        if (consumptionType == SAVING) {
            price = post.getPrice();
        }
        return new Consumption(post, consumptionType, price, year, month);
    }

    private void validate(final Long price, final Integer consumptionYear, final Integer consumptionMonth) {
        validatePrice(price);
        validateConsumptionYear(consumptionYear);
        validateConsumptionMonth(consumptionMonth);
        validateConsumptionYearMonth(consumptionYear, consumptionMonth);
    }

    private void validatePrice(final Long price) {
        if (Objects.isNull(price) || price < MIN_PRICE || price > MAX_PRICE) {
            throw new EdonymyeonException(CONSUMPTION_PRICE_ILLEGAL_SIZE);
        }
    }

    private void validateConsumptionYear(final Integer consumptionYear) {
        if (Objects.isNull(consumptionYear) || consumptionYear < MIN_YEAR) {
            throw new EdonymyeonException(CONSUMPTION_YEAR_ILLEGAL);
        }
    }

    private void validateConsumptionMonth(final Integer consumptionMonth) {
        if (Objects.isNull(consumptionMonth) || consumptionMonth < MIN_MONTH || consumptionMonth > MAX_MONTH) {
            throw new EdonymyeonException(CONSUMPTION_MONTH_ILLEGAL);
        }
    }

    private void validateConsumptionYearMonth(final Integer consumptionYear, final Integer consumptionMonth) {
        LocalDate currentDate = LocalDate.now();
        LocalDate inputDate = LocalDate.of(consumptionYear, consumptionMonth, 1);

        if (inputDate.isAfter(currentDate)) {
            throw new EdonymyeonException(CONSUMPTION_YEAR_MONTH_ILLEGAL);
        }
    }
}
