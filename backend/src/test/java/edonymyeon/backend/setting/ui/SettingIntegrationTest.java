package edonymyeon.backend.setting.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.setting.application.dto.SettingRequest;
import edonymyeon.backend.setting.domain.SettingType;
import edonymyeon.backend.support.EdonymyeonRestAssured;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class SettingIntegrationTest extends IntegrationFixture {

    private static void 설정값_검증(final List<Map<String, String>> settingDetails, final String 고유번호, final String 활성화여부) {
        settingDetails.stream().filter(setting -> Objects.equals(setting.get("preferenceType"), 고유번호))
                .findAny()
                .ifPresentOrElse(setting -> assertThat(setting).containsEntry("enabled", 활성화여부),
                        () -> fail("설정값을 찾지 못했습니다."));
    }

    @Test
    void 알림_설정_페이지를_조회한다(
            @Autowired AuthService authService,
            @Autowired ObjectMapper objectMapper
    ) throws IOException {
        final String email = "test@gmail.com";
        final String password = "test123!";
        authService.joinMember(new JoinRequest(email, password, "nickname", "testDeviceToken"));

        final String sessionId = 로그인(email, password);
        System.out.println("sessionId = " + sessionId);

        ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .sessionId(sessionId)
                .build()
                .when()
                .get("/preference/notification")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract();

        final Map<String, List<Map<String, String>>> settings = objectMapper.readValue(response.body().asByteArray(),
                new TypeReference<>() {
                });
        assertThat(settings.get("notifications")).hasSize(5);
    }

    @Test
    void 특정_설정을_토글링한다(
            @Autowired AuthService authService,
            @Autowired ObjectMapper objectMapper
    ) throws IOException {
        final String 이메일 = "test@gmail.com";
        final String 비밀번호 = "test123!";
        authService.joinMember(new JoinRequest(이메일, 비밀번호, "nickname", "testDeviceToken"));
        final String sessionId = 로그인(이메일, 비밀번호);

        final SettingType 반응_열건당_알림_설정정보 = SettingType.NOTIFICATION_PER_10_THUMBS;
        ExtractableResponse<Response> response = EdonymyeonRestAssured.builder()
                .version("1.0.0")
                .sessionId(sessionId)
                .build()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SettingRequest(반응_열건당_알림_설정정보.getSerialNumber()))
                .when()
                .post("/preference/notification")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();

        final Map<String, List<Map<String, String>>> settings = objectMapper.readValue(response.body().asByteArray(),
                new TypeReference<>() {
                });
        final List<Map<String, String>> settingDetails = settings.get("notifications");
        assertThat(settingDetails).hasSize(5);

        설정값_검증(settingDetails, SettingType.NOTIFICATION.getSerialNumber(), "true");
        설정값_검증(settingDetails, SettingType.NOTIFICATION_PER_10_THUMBS.getSerialNumber(), "true");
        설정값_검증(settingDetails, SettingType.NOTIFICATION_PER_THUMBS.getSerialNumber(), "false");
    }
}
