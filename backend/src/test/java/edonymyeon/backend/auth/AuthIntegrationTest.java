package edonymyeon.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edonymyeon.backend.auth.application.KakaoAuthResponseProvider;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.domain.TokenGenerator;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import edonymyeon.backend.member.domain.SocialInfo.SocialType;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.support.IntegrationFixture;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Base64;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class AuthIntegrationTest extends IntegrationFixture {

    @MockBean
    private KakaoAuthResponseProvider kakaoAuthResponseProvider;

    @Test
    public void 이메일_중복을_체크한다_중복X() {
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
    public void 이메일_중복을_체크한다_중복O() {
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
    public void 닉네임_중복을_체크한다_중복X() {
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
    public void 닉네임_중복을_체크한다_중복O() {
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
    public void 회원가입_성공() {
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
    public void 실제_회원가입_요청시_이메일_중복() {
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
    public void 실제_회원가입_요청시_닉네임_중복() {
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
    public void 쿠키가_잘_등록되는지_확인() {
        final Member member = memberTestSupport.builder()
                .build();

        final LoginRequest request = new LoginRequest(member.getEmail(), member.getPassword(), "unknownDevice");
        System.out.println("request = " + request);
        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/login")
                .then()
                .extract();

        String valueToEncode = request.email() + ":" + request.password();
        final String expectedCookieValue = Base64.getEncoder().encodeToString(valueToEncode.getBytes());

        assertSoftly(
                softly -> {
                    softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                    softly.assertThat(response.header(HttpHeaders.AUTHORIZATION))
                            .isEqualTo("Basic " + expectedCookieValue);
                });
    }

    @Test
    public void 카카오_로그인() {
        final Member member = memberTestSupport.builder()
                .socialInfo(SocialInfo.of(SocialType.KAKAO, 1L))
                .build();

        when(kakaoAuthResponseProvider.request(any())).thenReturn(new KakaoLoginResponse(member.getSocialInfo().getSocialId()));

        final KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("accessToken");

        final ExtractableResponse<Response> response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(kakaoLoginRequest)
                .when()
                .post("/auth/kakao/login")
                .then()
                .extract();

        TokenGenerator tokenGenerator = new TokenGenerator();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(response.header(HttpHeaders.AUTHORIZATION))
                    .isEqualTo(tokenGenerator.getBasicToken(member.getEmail(), member.getPassword()));
        });
    }

    @Test
    public void 카카오_처음_로그인시_회원가입(@Autowired MemberRepository memberRepository) {
        final Optional<Member> 카카오_로그인전_회원 = memberRepository.findBySocialInfo(SocialInfo.of(SocialType.KAKAO, 1L));
        when(kakaoAuthResponseProvider.request(any())).thenReturn(new KakaoLoginResponse(1L));

        final KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest("accessToken");

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
}
