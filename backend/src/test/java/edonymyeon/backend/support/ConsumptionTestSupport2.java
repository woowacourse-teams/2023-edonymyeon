package edonymyeon.backend.support;

import static edonymyeon.backend.consumption.domain.ConsumptionType.PURCHASE;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsumptionTestSupport2 {

    private static final ConsumptionType DEFAULT_CONSUMPTION_TYPE = PURCHASE;

    private static final Long DEFAULT_CONSUMPTION_PRICE = 1_000L;

    private static final int DEFAULT_CONSUMPTION_YEAR = 2023;

    private static final int DEFAULT_CONSUMPTION_MONTH = 8;

    private final PostTestSupport postTestSupport;

    private final ConsumptionRepository consumptionRepository;

    public ConsumptionTestSupport2.ConsumptionBuilder builder() {
        return new ConsumptionTestSupport2.ConsumptionBuilder();
    }

    public final class ConsumptionBuilder {

        private Post post;

        private ConsumptionType consumptionType;

        private Long price;

        private Integer consumptionYear;

        private Integer consumptionMonth;

        public ConsumptionTestSupport2.ConsumptionBuilder post(final Post post) {
            this.post = post;
            return this;
        }

        public ConsumptionTestSupport2.ConsumptionBuilder consumptionType(final ConsumptionType consumptionType) {
            this.consumptionType = consumptionType;
            return this;
        }

        public ConsumptionTestSupport2.ConsumptionBuilder price(final Long price) {
            this.price = price;
            return this;
        }

        public ConsumptionTestSupport2.ConsumptionBuilder consumptionYear(final Integer consumptionYear) {
            this.consumptionYear = consumptionYear;
            return this;
        }

        public ConsumptionTestSupport2.ConsumptionBuilder consumptionMonth(final Integer consumptionMonth) {
            this.consumptionMonth = consumptionMonth;
            return this;
        }

        public Consumption build() {
            return consumptionRepository.save(
                    Consumption.of(
                            post == null ? postTestSupport.builder().build() : post,
                            consumptionType == null ? DEFAULT_CONSUMPTION_TYPE : consumptionType,
                            price == null ? DEFAULT_CONSUMPTION_MONTH : price,
                            consumptionYear == null ? DEFAULT_CONSUMPTION_YEAR : consumptionYear,
                            consumptionMonth == null ? DEFAULT_CONSUMPTION_MONTH : consumptionMonth
                    )
            );
        }
    }
}
