package edonymyeon.backend.support;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.content.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsumptionTestSupport {

    private final ConsumptionRepository consumptionRepository;

    private final PostTestSupport postTestSupport;

    public ConsumptionBuilder builder() {
        return new ConsumptionBuilder();
    }

    public final class ConsumptionBuilder {

        private Post post;

        private ConsumptionType consumptionType;

        private Long price;

        private Integer consumptionYear;

        private Integer consumptionMonth;

        public ConsumptionBuilder post(final Post post) {
            this.post = post;
            return this;
        }

        public ConsumptionBuilder consumptionType(final ConsumptionType consumptionType) {
            this.consumptionType = consumptionType;
            return this;
        }

        public ConsumptionBuilder price(final Long price) {
            this.price = price;
            return this;
        }

        public ConsumptionBuilder consumptionYear(final Integer consumptionYear) {
            this.consumptionYear = consumptionYear;
            return this;
        }

        public ConsumptionBuilder consumptionMonth(final Integer consumptionMonth) {
            this.consumptionMonth = consumptionMonth;
            return this;
        }

        public Consumption build() {
            return consumptionRepository.save(
                    Consumption.of(
                            post == null ? postTestSupport.builder().build() : post,
                            consumptionType == null ? ConsumptionType.PURCHASE : consumptionType,
                            price == null ? 0 : price,
                            consumptionYear == null ? 2001 : consumptionYear,
                            consumptionMonth == null ? 1 : consumptionMonth
                    )
            );
        }
    }
}
