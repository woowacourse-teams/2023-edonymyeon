package edonymyeon.backend.consumption.application;

import edonymyeon.backend.consumption.application.dto.ConsumptionPriceResponse;
import edonymyeon.backend.consumption.application.dto.RecentConsumptionsResponse;
import edonymyeon.backend.consumption.domain.ConsumptionsPerMonth;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.member.application.dto.MemberId;
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

    public static final int FIRST_DAY_OF_MONTH = 1;

    private final ConsumptionRepository consumptionRepository;

    /**
     * periodMonth는 최근 몇개월간의 소비금액을 조회하고 싶은지를 나타냅니다. 예) periodMonth=6 이면 최근 6개월
     **/
    public RecentConsumptionsResponse findRecentConsumptions(final MemberId memberId, final Integer periodMonth) {
        PeriodMonth.checkIllegalPeriodMonth(periodMonth);

        final LocalDate currentDate = LocalDate.now();
        final LocalDate startDate = currentDate.minusMonths(periodMonth - 1);

        final List<ConsumptionPriceResponse> consumptionPriceResponses = new ArrayList<>();
        for (int i = 0; i < periodMonth; i++) {
            final LocalDate thisMonth = startDate.plusMonths(i);
            final ConsumptionsPerMonth consumptionsPerMonth = findConsumptionsPerMonthByMemberId(memberId, thisMonth);
            consumptionPriceResponses.add(ConsumptionPriceResponse.from(consumptionsPerMonth));
        }
        return RecentConsumptionsResponse.of(startDate, currentDate, consumptionPriceResponses);
    }

    private ConsumptionsPerMonth findConsumptionsPerMonthByMemberId(final MemberId memberId,
                                                                    final LocalDate thisMonth) {
        return new ConsumptionsPerMonth(consumptionRepository.findByMemberIdAndConsumptionDateBetween(
                memberId.id(),
                getFirstDateOfMonth(thisMonth),
                getLastDateOfMonth(thisMonth)
        ));
    }

    private LocalDate getFirstDateOfMonth(final LocalDate thisMonth) {
        return thisMonth.withDayOfMonth(FIRST_DAY_OF_MONTH);
    }

    private LocalDate getLastDateOfMonth(final LocalDate thisMonth) {
        final int lastDayOfThisMonth = thisMonth.lengthOfMonth();
        return thisMonth.withDayOfMonth(lastDayOfThisMonth);
    }
}
