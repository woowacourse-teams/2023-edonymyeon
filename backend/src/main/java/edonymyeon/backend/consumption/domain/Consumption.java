package edonymyeon.backend.consumption.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_MONTH_ILLEGAL;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_PRICE_ILLEGAL_SIZE;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_YEAR_ILLEGAL;

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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Consumption {

    private static final long MAX_PRICE = 10_000_000_000L;
    private static final int MIN_PRICE = 0;
    private static final int MIN_YEAR = 0;
    private static final int MAX_MONTH = 12;
    private static final int MIN_MONTH = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // todo: 게시글이 삭제되어도, 소비확정, 구매확정 내역은 남아있어야 가격 계산이 가능하다. 지워지면 안됨. <-과연 그럴까?!
    // todo: 또, 게시글 삭제할때 확정내역이 연관되어 있는 것도 해결해야 함
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
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

    public Consumption(
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

    private void validate(final Long price, final Integer consumptionYear, final Integer consumptionMonth) {
        validatePrice(price);
        validateConsumptionYear(consumptionYear);
        validateConsumptionMonth(consumptionMonth);
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
}
