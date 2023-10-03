package edonymyeon.backend.support;

import static org.springframework.http.HttpHeaders.COOKIE;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.util.Objects;

/**
 * 버저닝과 세션 id를 편하게 설정하게 도와줍니다.
 */
public class EdonymyeonRestAssured {

    private static final String VERSION_HEADER = "X-API-VERSION";

    public static EdonymyeonRestAssuredBuilder builder() {
        return new EdonymyeonRestAssuredBuilder();
    }

    public static class EdonymyeonRestAssuredBuilder {

        private static final int INIT_VERSION = 1;

        private int version = INIT_VERSION;
        private String sessionId;

        public EdonymyeonRestAssuredBuilder version(final int version) {
            this.version = version;
            return this;
        }

        public EdonymyeonRestAssuredBuilder sessionId(final String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public RequestSpecification build() {
            if (Objects.isNull(sessionId)) {
                return RestAssured.given()
                        .header(VERSION_HEADER, version);
            }
            return RestAssured.given()
                    .header(VERSION_HEADER, version)
                    .sessionId(sessionId);
        }
    }
}
