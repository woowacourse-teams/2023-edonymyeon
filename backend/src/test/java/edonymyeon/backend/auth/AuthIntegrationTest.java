package edonymyeon.backend.auth;

import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.KakaoAuthResponseProvider;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.application.dto.LogoutRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import edonymyeon.backend.member.domain.SocialInfo.SocialType;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.support.TestMemberBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
class AuthIntegrationTest extends IntegrationFixture {

    @MockBean
    private KakaoAuthResponseProvider kakaoAuthResponseProvider;

    @Test
    void 이메일_중복을_체크한다_중복X() {
        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "email")
                .param("value", "email@naver.com")
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isTrue();
                }
        );
    }

    @Test
    void 이메일_중복을_체크한다_중복O() {
        final Member member = memberTestSupport.builder()
                .email("email@naver.com")
                .build();

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "email")
                .param("value", member.getEmail())
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isFalse();
                }
        );
    }

    @Test
    void 닉네임_중복을_체크한다_중복X() {
        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "nickname")
                .param("value", "2rinebabo")
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isTrue();
                }
        );
    }

    @Test
    void 닉네임_중복을_체크한다_중복O() {
        final Member member = memberTestSupport.builder()
                .nickname("2rinebabo")
                .build();

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .param("target", "nickname")
                .param("value", member.getNickname())
                .when()
                .get("/join")
                .then()
                .extract();

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softAssertions.assertThat(response.as(DuplicateCheckResponse.class).isUnique()).isFalse();
                }
        );
    }

    @Test
    void 회원가입_성공() {
        final JoinRequest request = new JoinRequest("email@naver.com", "password123!", "kerrobabo", "unknownDevice");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/join")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 실제_회원가입_요청시_이메일_중복() {
        final String duplicatedEmail = "email";

        memberTestSupport.builder()
                .email(duplicatedEmail)
                .build();

        final JoinRequest request = new JoinRequest(duplicatedEmail, "password123!", "kerrobabo", "unknownDevice");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/join")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 실제_회원가입_요청시_닉네임_중복() {
        final String duplicatedNickname = "nickname";

        memberTestSupport.builder()
                .nickname(duplicatedNickname)
                .build();

        final JoinRequest request = new JoinRequest("foxbabo", "password123!", duplicatedNickname, "unknownDevice");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/join")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 쿠키가_잘_등록되는지_확인(@Autowired AuthService authService) {
        final JoinRequest joinRequest = new JoinRequest("test@email.com", "password123!", "nickName", "unknownDevice");
        authService.joinMember(joinRequest);

        final LoginRequest request = new LoginRequest(joinRequest.email(), joinRequest.password(),
                joinRequest.deviceToken());

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/login")
                .then()
                .extract();

        assertSoftly(
                softly -> {
                    softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softly.assertThat(response.header(HttpHeaders.SET_COOKIE))
                            .contains(response.sessionId());
                });
    }

    @Test
    void 카카오_로그인() {
        final Member member = memberTestSupport.builder()
                .socialInfo(SocialInfo.of(SocialType.KAKAO, 1L))
                .build();

        when(kakaoAuthResponseProvider.request(any())).thenReturn(
                new KakaoLoginResponse(member.getSocialInfo().getSocialId()));

        final KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("accessToken", "testDeviceToken");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(kakaoLoginRequest)
                .when()
                .post("/auth/kakao/login")
                .then()
                .extract();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(response.header(HttpHeaders.SET_COOKIE))
                    .contains(response.sessionId());
        });
    }

    @Test
    void 카카오_처음_로그인시_회원가입(@Autowired MemberRepository memberRepository) {
        final Optional<Member> 카카오_로그인전_회원 = memberRepository.findBySocialInfo(SocialInfo.of(SocialType.KAKAO, 1L));
        when(kakaoAuthResponseProvider.request(any())).thenReturn(new KakaoLoginResponse(1L));

        final KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("accessToken", "testDeviceToken");

        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(kakaoLoginRequest)
                .when()
                .post("/auth/kakao/login");

        final Optional<Member> 카카오_로그인후_회원 = memberRepository.findBySocialInfo(SocialInfo.of(SocialType.KAKAO, 1L));

        assertSoftly(softly -> {
            softly.assertThat(카카오_로그인전_회원).isEmpty();
            softly.assertThat(카카오_로그인후_회원).isPresent();
        });
    }

    @Test
    void 세션만료된_세션정보로_회원_정보_조회시_예외처리() {
        final Member member = memberTestSupport.builder().build();

        final ExtractableResponse<Response> 로그인_결과 = 로그인_요청(member);

        final String cookieValue = 로그인_결과.header(HttpHeaders.SET_COOKIE);
        final Header header = new Header(HttpHeaders.COOKIE, cookieValue);

        로그아웃_요청(member, header);

        final ExtractableResponse<Response> 내가_쓴글_조회 = RestAssured
                .given()
                .header(header)
                .when()
                .get("/profile/my-posts")
                .then()
                .extract();

        final JsonPath errorResponse = 내가_쓴글_조회.body().jsonPath();
        assertSoftly(
                softly -> {
                    softly.assertThat(내가_쓴글_조회.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                    softly.assertThat(errorResponse.getInt("errorCode")).isEqualTo(AUTHORIZATION_EMPTY.getCode());
                    softly.assertThat(errorResponse.getString("errorMessage"))
                            .isEqualTo(AUTHORIZATION_EMPTY.getMessage());
                }
        );
    }

    private ExtractableResponse<Response> 로그인_요청(Member member) {
        final String deviceToken = member.getDevices().get(0).getDeviceToken();
        final LoginRequest loginRequest = new LoginRequest(member.getEmail(), TestMemberBuilder.getRawPassword(),
                deviceToken);

        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/login")
                .then()
                .extract();
    }

    private ExtractableResponse<Response> 로그아웃_요청(Member member, Header header) {
        final String deviceToken = member.getDevices().get(0).getDeviceToken();
        final LogoutRequest logoutRequest = new LogoutRequest(deviceToken);

        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header(header)
                .body(logoutRequest)
                .when()
                .post("/logout")
                .then()
                .extract();
    }
}
