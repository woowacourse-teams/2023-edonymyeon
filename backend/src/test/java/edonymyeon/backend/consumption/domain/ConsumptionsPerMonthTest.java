package edonymyeon.backend.consumption.domain;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ConsumptionsPerMonthTest {

    @Test
    void 소비내역을_달별로_분류한다() {
        final Member 회원 = new Member("email", "password123!", "nickname", null);
        final Post 게시글1 = new Post("title", "content", 1_000L, 회원);
        final Post 게시글2 = new Post("title", "content", 2_000L, 회원);

        final Consumption 칠월_소비내역 = Consumption.of(게시글1, ConsumptionType.PURCHASE, 1_000L, 2023, 7);
        final Consumption 팔월_소비내역 = Consumption.of(게시글2, ConsumptionType.PURCHASE, 3_000L, 2023, 8);
        List<Consumption> consumptions = new ArrayList<>(List.of(칠월_소비내역, 팔월_소비내역));

        final List<ConsumptionsPerMonth> 두달_소비금액 = ConsumptionsPerMonth.of(
                consumptions,
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 8, 31)
        );

        final ConsumptionsPerMonth 칠월_소비금액 = 두달_소비금액.get(0);
        final ConsumptionsPerMonth 팔월_소비금액 = 두달_소비금액.get(1);

        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(칠월_소비금액.getConsumptions().size()).isEqualTo(1);
                    assertThat(칠월_소비금액.calculateTotalPurchasePrice()).isEqualTo(1_000L);
                    assertThat(칠월_소비금액.calculateTotalSavingPrice()).isEqualTo(0L);

                    assertThat(팔월_소비금액.getConsumptions().size()).isEqualTo(1);
                    assertThat(팔월_소비금액.calculateTotalPurchasePrice()).isEqualTo(3_000L);
                    assertThat(팔월_소비금액.calculateTotalSavingPrice()).isEqualTo(0L);
                }
        );
    }

    @Test
    void 소비내역이_없을_경우_구매금액의합은_0_절약금액의합도_0() {
        final Member 회원 = new Member("email", "password123!", "nickname", null);
        List<Consumption> consumptions = new ArrayList<>();

        final List<ConsumptionsPerMonth> 두달_소비금액 = ConsumptionsPerMonth.of(
                consumptions,
                LocalDate.of(2023, 7, 1),
                LocalDate.of(2023, 8, 31)
        );

        final ConsumptionsPerMonth 칠월_소비금액 = 두달_소비금액.get(0);
        final ConsumptionsPerMonth 팔월_소비금액 = 두달_소비금액.get(1);

        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(칠월_소비금액.getConsumptions().size()).isEqualTo(0);
                    assertThat(칠월_소비금액.calculateTotalPurchasePrice()).isEqualTo(0L);
                    assertThat(칠월_소비금액.calculateTotalSavingPrice()).isEqualTo(0L);

                    assertThat(팔월_소비금액.getConsumptions().size()).isEqualTo(0);
                    assertThat(팔월_소비금액.calculateTotalPurchasePrice()).isEqualTo(0L);
                    assertThat(팔월_소비금액.calculateTotalSavingPrice()).isEqualTo(0L);
                }
        );
    }
}
