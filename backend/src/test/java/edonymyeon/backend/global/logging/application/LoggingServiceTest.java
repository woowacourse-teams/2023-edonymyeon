package edonymyeon.backend.global.logging.application;

import static org.assertj.core.api.SoftAssertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.global.logging.application.LoggingFileReader;
import edonymyeon.backend.global.logging.application.LoggingService;
import edonymyeon.backend.global.logging.ui.dto.LoggingResponse;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class LoggingServiceTest {
    private LoggingFileReader loggingFileReader = new LoggingFileReader(new ObjectMapper());
    private LoggingService loggingService = new LoggingService(loggingFileReader);

    @Test
    void info로그가_위치한_파일을_읽어_객체_리스트로_파싱한다() {
        LoggingResponse loggingResponse = loggingService.findLogs(
                () -> Paths.get("src/test/java/edonymyeon/backend/global/logging/application/info.log").toAbsolutePath());

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(loggingResponse.response().size()).isEqualTo(4);
        });
        assertLogContents(loggingResponse);
    }

    private static void assertLogContents(final LoggingResponse loggingResponse) {
        final var firstLog = loggingResponse.response().get(0);
        final var logWithStatusInfo = loggingResponse.response().get(2);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(loggingResponse.response().size()).isEqualTo(4);

            softAssertions.assertThat(firstLog.keySet()).containsExactlyInAnyOrder("timestamp", "message");
            softAssertions.assertThat(firstLog.get("timestamp")).isEqualTo("20230802 004234.259000");
            softAssertions.assertThat(firstLog.get("message")).isEqualTo("HikariPool-1 - Starting...");

            softAssertions.assertThat(logWithStatusInfo.keySet())
                    .containsExactlyInAnyOrder("statuscode", "request-identifier", "uri", "timestamp", "message");
            softAssertions.assertThat(logWithStatusInfo.get("statuscode")).isEqualTo("404");
            softAssertions.assertThat(logWithStatusInfo.get("request-identifier")).isEqualTo("24fb1e2d-b644-4dae-a62f-9f352cbe344e");
            softAssertions.assertThat(logWithStatusInfo.get("uri")).isEqualTo("/error");
            softAssertions.assertThat(logWithStatusInfo.get("timestamp")).isEqualTo("20230802 002226.756000");
            softAssertions.assertThat(logWithStatusInfo.get("message")).isEqualTo("body  ");
        });
    }
}
