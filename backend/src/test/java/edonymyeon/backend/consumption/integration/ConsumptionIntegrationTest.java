package edonymyeon.backend.consumption.integration;

import static edonymyeon.backend.consumption.integration.steps.ConsumptionSteps.특정_기간의_소비금액을_확인한다;
import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.support.ConsumptionTestSupport2;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class ConsumptionIntegrationTest extends IntegrationTest {

    @Autowired
    private ConsumptionTestSupport2 consumptionTestSupport2;

    @Test
    void 최근_한달간의_소비금액을_확인한다() {
        LocalDate 최근_날짜 = java.time.LocalDate.now();
        final int 년 = 최근_날짜.getYear();
        final int 월 = 최근_날짜.getMonth().getValue();
        final Member 사용자 = memberTestSupport.builder().build();
        final long 절약_금액_합 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(10, 사용자, 년, 월);
        final long 구매_금액_합 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(5, 사용자, 년, 월);

        final ExtractableResponse<Response> 조회_응답 = 특정_기간의_소비금액을_확인한다(1, 사용자);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String 최근_년_달 = 최근_날짜.format(formatter);
        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(조회_응답.jsonPath().getString("startMonth")).isEqualTo(최근_년_달);
                    assertThat(조회_응답.jsonPath().getString("endMonth")).isEqualTo(최근_년_달);
                    assertThat(조회_응답.jsonPath().getList("consumptions")).hasSize(1);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[0].savingPrice")).isEqualTo(절약_금액_합);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[0].purchasePrice")).isEqualTo(구매_금액_합);
                }
        );
    }

    private long 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(
            final Integer 개수,
            final Member 사용자,
            final Integer 년,
            final Integer 월
    ) {
        List<Consumption> 절약_내역 = new ArrayList<>();
        for (int i = 0; i < 개수; i++) {
            절약_내역.add(절약_내역을_생성한다(사용자, 년, 월));
        }
        return 소비_내역의_금액_합을_구한다(절약_내역);
    }

    private Consumption 절약_내역을_생성한다(final Member 사용자, final Integer 년, final Integer 월) {
        final Post 게시글 = postTestSupport.builder().member(사용자).build();
        return consumptionTestSupport2.builder()
                .post(게시글)
                .consumptionType(ConsumptionType.SAVING)
                .consumptionYear(년)
                .consumptionMonth(월)
                .build();
    }

    private long 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(
            final Integer 개수,
            final Member 사용자,
            final Integer 년,
            final Integer 월
    ) {
        List<Consumption> 구매_내역 = new ArrayList<>();
        for (int i = 0; i < 개수; i++) {
            구매_내역.add(구매_내역을_생성한다(사용자, 년, 월));
        }
        return 소비_내역의_금액_합을_구한다(구매_내역);
    }

    private Consumption 구매_내역을_생성한다(final Member 사용자, final Integer 년, final Integer 월) {
        final Post 게시글 = postTestSupport.builder().member(사용자).build();
        return consumptionTestSupport2.builder()
                .post(게시글)
                .consumptionType(ConsumptionType.PURCHASE)
                .price(10_000L)
                .consumptionYear(년)
                .consumptionMonth(월)
                .build();
    }

    private long 소비_내역의_금액_합을_구한다(final List<Consumption> 소비_내역) {
        return 소비_내역.stream()
                .mapToLong(Consumption::getPrice)
                .sum();
    }

    @Test
    void 최근_여섯달간의_소비금액을_확인한다() {
        LocalDate 최근_날짜 = java.time.LocalDate.now();
        final LocalDate 최근_날짜_전달 = 최근_날짜.minusMonths(1);
        final LocalDate 최근_날짜_전전달 = 최근_날짜.minusMonths(2);
        final LocalDate 최근_날짜_전전전달 = 최근_날짜.minusMonths(3);
        final LocalDate 최근_날짜_전전전전달 = 최근_날짜.minusMonths(4);
        final LocalDate 시작_날짜 = 최근_날짜.minusMonths(5);
        final Member 사용자 = memberTestSupport.builder().build();

        final long 절약_금액_합_1 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(10, 사용자, 시작_날짜.getYear(),
                시작_날짜.getMonth().getValue());
        final long 구매_금액_합_1 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(5, 사용자, 시작_날짜.getYear(),
                시작_날짜.getMonth().getValue());
        final long 절약_금액_합_2 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(20, 사용자, 최근_날짜_전전전전달.getYear(),
                최근_날짜_전전전전달.getMonth().getValue());
        final long 구매_금액_합_2 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(6, 사용자, 최근_날짜_전전전전달.getYear(),
                최근_날짜_전전전전달.getMonth().getValue());
        final long 절약_금액_합_3 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(30, 사용자, 최근_날짜_전전전달.getYear(),
                최근_날짜_전전전달.getMonth().getValue());
        final long 구매_금액_합_3 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(7, 사용자, 최근_날짜_전전전달.getYear(),
                최근_날짜_전전전달.getMonth().getValue());
        final long 절약_금액_합_4 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(40, 사용자, 최근_날짜_전전달.getYear(),
                최근_날짜_전전달.getMonth().getValue());
        final long 구매_금액_합_4 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(8, 사용자, 최근_날짜_전전달.getYear(),
                최근_날짜_전전달.getMonth().getValue());
        final long 절약_금액_합_5 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(50, 사용자, 최근_날짜_전달.getYear(),
                최근_날짜_전달.getMonth().getValue());
        final long 구매_금액_합_5 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(9, 사용자, 최근_날짜_전달.getYear(),
                최근_날짜_전달.getMonth().getValue());
        final long 절약_금액_합_6 = 특정_달의_절약_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(60, 사용자, 최근_날짜.getYear(),
                최근_날짜.getMonth().getValue());
        final long 구매_금액_합_6 = 특정_달의_구매_내역을_특정_개수만큼_생성하고_금액의_합을_가져온다(10, 사용자, 최근_날짜.getYear(),
                최근_날짜.getMonth().getValue());

        final ExtractableResponse<Response> 조회_응답 = 특정_기간의_소비금액을_확인한다(6, 사용자);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String 시작_년_달 = 시작_날짜.format(formatter);
        String 최근_년_달 = 최근_날짜.format(formatter);
        SoftAssertions.assertSoftly(
                SoftAssertions -> {
                    assertThat(조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
                    assertThat(조회_응답.jsonPath().getString("startMonth")).isEqualTo(시작_년_달);
                    assertThat(조회_응답.jsonPath().getString("endMonth")).isEqualTo(최근_년_달);
                    assertThat(조회_응답.jsonPath().getList("consumptions")).hasSize(6);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[0].savingPrice")).isEqualTo(절약_금액_합_1);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[0].purchasePrice")).isEqualTo(구매_금액_합_1);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[1].savingPrice")).isEqualTo(절약_금액_합_2);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[1].purchasePrice")).isEqualTo(구매_금액_합_2);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[2].savingPrice")).isEqualTo(절약_금액_합_3);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[2].purchasePrice")).isEqualTo(구매_금액_합_3);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[3].savingPrice")).isEqualTo(절약_금액_합_4);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[3].purchasePrice")).isEqualTo(구매_금액_합_4);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[4].savingPrice")).isEqualTo(절약_금액_합_5);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[4].purchasePrice")).isEqualTo(구매_금액_합_5);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[5].savingPrice")).isEqualTo(절약_금액_합_6);
                    assertThat(조회_응답.jsonPath().getLong("consumptions[5].purchasePrice")).isEqualTo(구매_금액_합_6);
                }
        );
    }
}
