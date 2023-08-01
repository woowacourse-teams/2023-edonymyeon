package edonymyeon.backend.consumption.application;

import edonymyeon.backend.consumption.application.dto.ConsumptionPriceResponse;
import edonymyeon.backend.consumption.application.dto.RecentConsumptionsResponse;
import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ConsumptionStatisticsService {

    private final ConsumptionRepository consumptionRepository;

    public RecentConsumptionsResponse findRecentConsumptions(final MemberIdDto memberId, final Integer periodMonth) {
        List<ConsumptionPriceResponse> consumptionPriceResponses = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        final LocalDate startDate = currentDate.minusMonths(periodMonth - 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String startMonth = startDate.format(formatter);
        String endMonth = currentDate.format(formatter);

        for (int i = 0; i < periodMonth; i++) {
            long purchasePrice = 0;
            long savingPrice = 0;
            final LocalDate nowDate = startDate.plusMonths(i);
            final List<Consumption> consumptions = consumptionRepository.findByMemberIdAndYearAndMonth(
                    memberId.id(),
                    nowDate.getYear(),
                    nowDate.getMonth().getValue()
            );
            for (Consumption consumption : consumptions) {
                if (consumption.getConsumptionType() == ConsumptionType.SAVING) {
                    savingPrice += consumption.getPrice();
                    continue;
                }
                purchasePrice += consumption.getPrice();
            }
            consumptionPriceResponses.add(new ConsumptionPriceResponse(purchasePrice, savingPrice));
        }
        return new RecentConsumptionsResponse(startMonth, endMonth, consumptionPriceResponses);
    }
}
