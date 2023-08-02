package edonymyeon.backend.consumption.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_PERIOD_MONTH_ILLEGAL;

import edonymyeon.backend.consumption.application.dto.ConsumptionPriceResponse;
import edonymyeon.backend.consumption.application.dto.RecentConsumptionsResponse;
import edonymyeon.backend.consumption.domain.ConsumptionsPerMonth;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ConsumptionService {

    private final ConsumptionRepository consumptionRepository;

    public RecentConsumptionsResponse findRecentConsumptions(final MemberIdDto memberId, final Integer periodMonth) {
        if (isIllegalPeriodMonth(periodMonth)) {
            throw new EdonymyeonException(CONSUMPTION_PERIOD_MONTH_ILLEGAL);
        }
        final LocalDate currentDate = LocalDate.now();
        final LocalDate startDate = currentDate.minusMonths(periodMonth - 1);

        final List<ConsumptionPriceResponse> consumptionPriceResponses = new ArrayList<>();
        for (int i = 0; i < periodMonth; i++) {
            final LocalDate nowDate = startDate.plusMonths(i);
            final ConsumptionsPerMonth consumptionsPerMonth = new ConsumptionsPerMonth(
                    consumptionRepository.findByMemberIdAndYearAndMonth(
                            memberId.id(),
                            nowDate.getYear(),
                            nowDate.getMonth().getValue()
                    ));
            consumptionPriceResponses.add(new ConsumptionPriceResponse(
                    consumptionsPerMonth.calculateTotalPurchasePrice(),
                    consumptionsPerMonth.calculateTotalSavingPrice()
            ));
        }
        return RecentConsumptionsResponse.of(startDate, currentDate, consumptionPriceResponses);
    }

    private boolean isIllegalPeriodMonth(final Integer periodMonth) {
        return periodMonth != 1 && periodMonth != 6;
    }
}
