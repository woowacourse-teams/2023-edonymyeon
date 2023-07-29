package edonymyeon.backend.member.integration;

import static edonymyeon.backend.member.integration.memberConsumptionSteps.MemberConsumptionSteps.구매_확정_요청을_보낸다;
import static edonymyeon.backend.member.integration.memberConsumptionSteps.MemberConsumptionSteps.절약_확정_요청을_보낸다;
import static edonymyeon.backend.member.integration.memberConsumptionSteps.MemberConsumptionSteps.확정_취소_요청을_보낸다;
import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.IntegrationTest;
import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class MemberConsumptionIntegrationTest {

    @Nested
    class 구매_확정할_때 extends IntegrationTest {

        @Test
        void 게시글의_작성자면_성공한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final PurchaseConfirmRequest 구매_확정_요청 = new PurchaseConfirmRequest(10000L, 2023, 7);

            // when
            final ExtractableResponse<Response> 구매_확정_응답 = 구매_확정_요청을_보낸다(글쓴이, 게시글, 구매_확정_요청);

            // then
            assertThat(구매_확정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 게시글의_작성자가_아니면_실패한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final Member 다른_사람 = memberTestSupport.builder().build();
            final PurchaseConfirmRequest 구매_확정_요청 = new PurchaseConfirmRequest(10000L, 2023, 7);

            // when
            final ExtractableResponse<Response> 구매_확정_응답 = 구매_확정_요청을_보낸다(다른_사람, 게시글, 구매_확정_요청);

            // then
            final ExceptionResponse 예외_응답 = 구매_확정_응답.as(ExceptionResponse.class);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        assertThat(구매_확정_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                        assertThat(예외_응답.errorCode()).isEqualTo(ExceptionInformation.POST_MEMBER_NOT_SAME.getCode());
                        assertThat(예외_응답.errorMessage()).isEqualTo(
                                ExceptionInformation.POST_MEMBER_NOT_SAME.getMessage());
                    }
            );
        }

        @Test
        void 이미_소비_확정_내역이_존재하면_실패한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final PurchaseConfirmRequest 구매_확정_요청 = new PurchaseConfirmRequest(10000L, 2023, 7);
            구매_확정_요청을_보낸다(글쓴이, 게시글, 구매_확정_요청);

            // when
            final ExtractableResponse<Response> 구매_확정_응답 = 구매_확정_요청을_보낸다(글쓴이, 게시글, 구매_확정_요청);

            // then
            final ExceptionResponse 예외_응답 = 구매_확정_응답.as(ExceptionResponse.class);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        assertThat(구매_확정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(예외_응답.errorCode()).isEqualTo(
                                ExceptionInformation.CONSUMPTION_POST_ID_ALREADY_EXIST.getCode());
                        assertThat(예외_응답.errorMessage()).isEqualTo(
                                ExceptionInformation.CONSUMPTION_POST_ID_ALREADY_EXIST.getMessage());
                    }
            );
        }
    }

    @Nested
    class 절약_확정할_때 extends IntegrationTest {

        @Test
        void 게시글의_작성자면_성공한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final SavingConfirmRequest 절약_확정_요청 = new SavingConfirmRequest(2023, 7);

            // when
            final ExtractableResponse<Response> 절약_확정_응답 = 절약_확정_요청을_보낸다(글쓴이, 게시글, 절약_확정_요청);

            // then
            assertThat(절약_확정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 게시글의_작성자가_아니면_실패한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final Member 다른_사람 = memberTestSupport.builder().build();
            final SavingConfirmRequest 절약_확정_요청 = new SavingConfirmRequest(2023, 7);

            // when
            final ExtractableResponse<Response> 절약_확정_응답 = 절약_확정_요청을_보낸다(다른_사람, 게시글, 절약_확정_요청);

            // then
            final ExceptionResponse 예외_응답 = 절약_확정_응답.as(ExceptionResponse.class);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        assertThat(절약_확정_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                        assertThat(예외_응답.errorCode()).isEqualTo(ExceptionInformation.POST_MEMBER_NOT_SAME.getCode());
                        assertThat(예외_응답.errorMessage()).isEqualTo(
                                ExceptionInformation.POST_MEMBER_NOT_SAME.getMessage());
                    }
            );
        }

        @Test
        void 이미_소비_확정_내역이_존재하면_실패한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final PurchaseConfirmRequest 구매_확정_요청 = new PurchaseConfirmRequest(10000L, 2023, 7);
            구매_확정_요청을_보낸다(글쓴이, 게시글, 구매_확정_요청);

            // when
            final SavingConfirmRequest 절약_확정_요청 = new SavingConfirmRequest(2023, 7);
            final ExtractableResponse<Response> 절약_확정_응답 = 절약_확정_요청을_보낸다(글쓴이, 게시글, 절약_확정_요청);

            // then
            final ExceptionResponse 예외_응답 = 절약_확정_응답.as(ExceptionResponse.class);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        assertThat(절약_확정_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(예외_응답.errorCode()).isEqualTo(
                                ExceptionInformation.CONSUMPTION_POST_ID_ALREADY_EXIST.getCode());
                        assertThat(예외_응답.errorMessage()).isEqualTo(
                                ExceptionInformation.CONSUMPTION_POST_ID_ALREADY_EXIST.getMessage());
                    }
            );
        }
    }

    @Nested
    class 확정_취소할_때 extends IntegrationTest {

        @Test
        void 게시글의_작성자면_성공한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final SavingConfirmRequest 절약_확정_요청 = new SavingConfirmRequest(2023, 7);
            절약_확정_요청을_보낸다(글쓴이, 게시글, 절약_확정_요청);

            // when
            final ExtractableResponse<Response> 확정_취소_응답 = 확정_취소_요청을_보낸다(글쓴이, 게시글);

            // then
            assertThat(확정_취소_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 게시글의_작성자가_아니면_실패한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();
            final Member 다른_사람 = memberTestSupport.builder().build();
            final SavingConfirmRequest 절약_확정_요청 = new SavingConfirmRequest(2023, 7);
            절약_확정_요청을_보낸다(글쓴이, 게시글, 절약_확정_요청);

            // when
            final ExtractableResponse<Response> 확정_취소_응답 = 확정_취소_요청을_보낸다(다른_사람, 게시글);

            // then
            final ExceptionResponse 예외_응답 = 확정_취소_응답.as(ExceptionResponse.class);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        assertThat(확정_취소_응답.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                        assertThat(예외_응답.errorCode()).isEqualTo(ExceptionInformation.POST_MEMBER_NOT_SAME.getCode());
                        assertThat(예외_응답.errorMessage()).isEqualTo(
                                ExceptionInformation.POST_MEMBER_NOT_SAME.getMessage());
                    }
            );
        }

        @Test
        void 취소해야_할_소비_확정_내역이_존재하지_않으면_실패한다() {
            // given
            final Member 글쓴이 = memberTestSupport.builder().build();
            final Post 게시글 = postTestSupport.builder().member(글쓴이).build();

            // when
            final ExtractableResponse<Response> 확정_취소_응답 = 확정_취소_요청을_보낸다(글쓴이, 게시글);

            // then
            final ExceptionResponse 예외_응답 = 확정_취소_응답.as(ExceptionResponse.class);
            SoftAssertions.assertSoftly(
                    softAssertions -> {
                        assertThat(확정_취소_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(예외_응답.errorCode()).isEqualTo(
                                ExceptionInformation.CONSUMPTION_POST_ID_NOT_FOUND.getCode());
                        assertThat(예외_응답.errorMessage()).isEqualTo(
                                ExceptionInformation.CONSUMPTION_POST_ID_NOT_FOUND.getMessage());
                    }
            );
        }
    }
}
