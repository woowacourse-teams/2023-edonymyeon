package edonymyeon.backend.scenario.post;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("게시글 작성 시나리오 테스트")
public class PostCreationScenarioTest {

    @Test
    void 로그인된_사용자는_게시글을_새로_작성할_수_있다() throws JsonProcessingException {
        final var response = RestAssured
                .given().log().all()
                .body(정상적으로_작성된_글())
                .auth().basic("testMember", "password1234")
                .when().log().all()
                .post("/posts")
                .then().log().all()
                .extract();

        final var responseBody = response.body();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED);
            softly.assertThat(response.header(HttpHeaders.LOCATION)).matches("/url/[0-9]");
            softly.assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
            softly.assertThat(responseBody.jsonPath().getInt("id")).isNotNull();
        });
    }

    @Test
    void 로그인되지_않은_사용자는_게시글을_작성할_수_없다() throws JsonProcessingException {
        final var response = RestAssured
                .given().log().all()
                .body(정상적으로_작성된_글())
                .auth().basic("testMember", "password1234")
                .when().log().all()
                .post("/posts")
                .then().log().all()
                .extract();

        final var responseBody = response.body();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            softly.assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//          TODO: 예외 메시지 작성 softly.assertThat(responseBody.jsonPath().getInt("id")).isNotNull();
//          TODO: 예외 자체코드 작성 softly.assertThat(responseBody.jsonPath().getInt("id")).isNotNull();
        });
    }

    @MethodSource("잘못된게시글양식")
    @ParameterizedTest(name = "{displayName} - {0}")
    void 게시글_양식에_맞지_않게_작성할_수_없다(String field, String requestBody, String exceptionCode, String exceptionMessage) {
        final var response = RestAssured
                .given().log().all()
                .body(requestBody)
                .auth().basic("testMember", "password1234")
                .when().log().all()
                .post("/posts")
                .then().log().all()
                .extract();

        final var responseBody = response.body();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            softly.assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//          TODO: 예외 메시지 작성 softly.assertThat(responseBody.jsonPath().getInt("id")).isNotNull();
//          TODO: 예외 자체코드 작성 softly.assertThat(responseBody.jsonPath().getInt("id")).isNotNull();
        });
    }

    private static String 정상적으로_작성된_글() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요? 우리집 강아지가 너무 귀엽습니다.");
        requestBody.put("content", "빨간염소를 꾸미고 SNS에 올리면 천 원이 대신 기부가 되는 빨간염소 챌린지! 챌린지를 통해 아프리카에 보내질 염소는 마을 주민들의 영양과 경제력까지 책임지며 마을이 자립할 수 있는 큰 힘이 되어줄 거예요. 지금, 아프리카에 보낼 나만의 염소를 꾸며보세요!");
        requestBody.put("price", 13_000);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    static Stream<Arguments> 잘못된게시글양식() throws JsonProcessingException {
        return Stream.of(
                Arguments.arguments("title", 제목이_비어있는_글(), "TODO: 예외 코드", "TODO: 예외 메시지"),
                Arguments.arguments("title", 제목이_최대_허용치를_넘어가는_글(), "TODO: 예외 코드", "TODO: 예외 메시지"),
                Arguments.arguments("content", 내용에_공백만_있는_글(), "TODO: 예외 코드", "TODO: 예외 메시지"),
                Arguments.arguments("content", 내용이_최대_허용치를_넘어가는_글(), "TODO: 예외 코드", "TODO: 예외 메시지"),
                Arguments.arguments("images", 최대_허용치를_넘게_담은_사진(), "TODO: 예외 코드", "TODO: 예외 메시지"),
                Arguments.arguments("price", 가격이_음수(), "TODO: 예외 코드", "TODO: 예외 메시지"),
                Arguments.arguments("price", 허용한_최대치를_넘어가는_금액(), "TODO: 예외 코드", "TODO: 예외 메시지")
        );
    }

    private static String 제목이_비어있는_글() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "");
        requestBody.put("content", "빨간염소를 꾸미고 SNS에 올리면 천 원이 대신 기부가 되는 빨간염소 챌린지! 챌린지를 통해 아프리카에 보내질 염소는 마을 주민들의 영양과 경제력까지 책임지며 마을이 자립할 수 있는 큰 힘이 되어줄 거예요. 지금, 아프리카에 보낼 나만의 염소를 꾸며보세요!");
        requestBody.put("price", 13_000);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    private static String 제목이_최대_허용치를_넘어가는_글() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요? 우리집 강아지가 너무 귀엽습니다.");
        requestBody.put("content", "빨간염소를 꾸미고 SNS에 올리면 천 원이 대신 기부가 되는 빨간염소 챌린지! 챌린지를 통해 아프리카에 보내질 염소는 마을 주민들의 영양과 경제력까지 책임지며 마을이 자립할 수 있는 큰 힘이 되어줄 거예요. 지금, 아프리카에 보낼 나만의 염소를 꾸며보세요!");
        requestBody.put("price", 13_000);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    private static String 내용에_공백만_있는_글() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요?");
        requestBody.put("content", " ");
        requestBody.put("price", 13_000);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    private static String 내용이_최대_허용치를_넘어가는_글() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요?");
        requestBody.put("content", "ㅐ".repeat(1001));
        requestBody.put("price", 13_000);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    private static String 최대_허용치를_넘게_담은_사진() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요?");
        requestBody.put("content", "빨간염소를 꾸미고 SNS에 올리면 천 원이 대신 기부가 되는 빨간염소 챌린지! 챌린지를 통해 아프리카에 보내질 염소는 마을 주민들의 영양과 경제력까지 책임지며 마을이 자립할 수 있는 큰 힘이 되어줄 거예요. 지금, 아프리카에 보낼 나만의 염소를 꾸며보세요!");
        requestBody.put("price", 13_000);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",

                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",

                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"
        ));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    private static String 가격이_음수() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요?");
        requestBody.put("content", "빨간염소를 꾸미고 SNS에 올리면 천 원이 대신 기부가 되는 빨간염소 챌린지! 챌린지를 통해 아프리카에 보내질 염소는 마을 주민들의 영양과 경제력까지 책임지며 마을이 자립할 수 있는 큰 힘이 되어줄 거예요. 지금, 아프리카에 보낼 나만의 염소를 꾸며보세요!");
        requestBody.put("price", -1);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }

    private static String 허용한_최대치를_넘어가는_금액() throws JsonProcessingException {
        final var requestBody = new HashMap<String, Object>();
        requestBody.put("title", "물건을 살까요 말까요?");
        requestBody.put("content", "빨간염소를 꾸미고 SNS에 올리면 천 원이 대신 기부가 되는 빨간염소 챌린지! 챌린지를 통해 아프리카에 보내질 염소는 마을 주민들의 영양과 경제력까지 책임지며 마을이 자립할 수 있는 큰 힘이 되어줄 거예요. 지금, 아프리카에 보낼 나만의 염소를 꾸며보세요!");
        requestBody.put("price", 10_000_000_001L);
        requestBody.put("images", List.of("https://img.freepik.com/free-photo/tropical-sea-beach_74190-175.jpg",
                "https://cdn.pixabay.com/photo/2018/06/13/18/20/waves-3473335_1280.jpg"));

        final var objectMapper = new ObjectMapper();
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(requestBody);
    }
}
