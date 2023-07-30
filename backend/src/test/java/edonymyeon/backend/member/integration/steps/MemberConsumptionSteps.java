package edonymyeon.backend.member.integration.steps;

import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.post.domain.Post;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class MemberConsumptionSteps {

    public static ExtractableResponse<Response> 구매_확정_요청을_보낸다(
            final Member 사용자,
            final Post 게시글,
            final PurchaseConfirmRequest 구매_확정_요청
    ) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(구매_확정_요청)
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .when()
                .post("/profile/myPosts/{postId}/purchase-confirm", 게시글.getId())
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 절약_확정_요청을_보낸다(
            final Member 사용자,
            final Post 게시글,
            final SavingConfirmRequest 절약_확정_요청
    ) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(절약_확정_요청)
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .when()
                .post("/profile/myPosts/{postId}/saving-confirm", 게시글.getId())
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 확정_취소_요청을_보낸다(final Member 사용자, final Post 게시글) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .when()
                .delete("/profile/myPosts/{postId}/confirm-remove", 게시글.getId())
                .then()
                .extract();
    }
}
