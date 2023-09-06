package edonymyeon.backend.comment.integration.steps;

import edonymyeon.backend.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.File;

public class CommentSteps {

    public static ExtractableResponse<Response> 댓글을_생성한다(
            final Long 게시글_id,
            final File 이미지,
            final String 내용,
            final Member 사용자
    ) {
        return RestAssured.given()
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .multiPart("image", 이미지)
                .multiPart("comment", 내용)
                .when()
                .post("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 댓글을_삭제한다(
            final Long 게시글_id,
            final Long 댓글_id,
            final Member 사용자
    ) {
        return RestAssured.given()
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .when()
                .delete("/posts/{postId}/comments/{commentId}", 게시글_id, 댓글_id)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> 게시물에_대한_댓글을_모두_조회한다(
            final Long 게시글_id,
            final Member 사용자
    ) {
        return RestAssured.given()
                .auth().preemptive().basic(사용자.getEmail(), 사용자.getPassword())
                .when()
                .get("/posts/{postId}/comments", 게시글_id)
                .then()
                .extract();
    }
}
