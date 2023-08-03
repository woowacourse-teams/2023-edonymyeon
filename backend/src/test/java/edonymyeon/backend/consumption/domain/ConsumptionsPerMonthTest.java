package edonymyeon.backend.consumption.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ConsumptionsPerMonthTest {

    @Test
    void 생성자에_null이_들어오면_예외가_발생한다() {
        assertThatThrownBy(() -> new ConsumptionsPerMonth(null))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage(ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTIONS_NULL.getMessage());
    }

    @Test
    void 년도가_일치하지_않는_소비내역이_있으면_예외가_발생한다() {
        final Member 회원 = new Member("email", "password123!", "nickname", null);
        final Post 게시글1 = new Post("title", "content", 1_000L, 회원);
        final Post 게시글2 = new Post("title", "content", 2_000L, 회원);

        final Consumption 소비내역1 = Consumption.of(게시글1, ConsumptionType.PURCHASE, 1_000L, 2023, 7);
        final Consumption 소비내역2 = Consumption.of(게시글2, ConsumptionType.PURCHASE, 1_000L, 2022, 7);
        List<Consumption> consumptions = new ArrayList<>(List.of(소비내역1, 소비내역2));

        assertThatThrownBy(() -> new ConsumptionsPerMonth(consumptions))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage(ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTIONS_PERIOD_NOT_SAME.getMessage());
    }

    @Test
    void 달이_일치하지_않는_소비내역이_있으면_예외가_발생한다() {
        final Member 회원 = new Member("email", "password123!", "nickname", null);
        final Post 게시글1 = new Post("title", "content", 1_000L, 회원);
        final Post 게시글2 = new Post("title", "content", 2_000L, 회원);

        final Consumption 소비내역1 = Consumption.of(게시글1, ConsumptionType.PURCHASE, 1_000L, 2023, 7);
        final Consumption 소비내역2 = Consumption.of(게시글2, ConsumptionType.PURCHASE, 1_000L, 2023, 6);
        List<Consumption> consumptions = new ArrayList<>(List.of(소비내역1, 소비내역2));

        assertThatThrownBy(() -> new ConsumptionsPerMonth(consumptions))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage(ExceptionInformation.BUSINESS_LOGIC_ERROR_CONSUMPTIONS_PERIOD_NOT_SAME.getMessage());
    }
}
