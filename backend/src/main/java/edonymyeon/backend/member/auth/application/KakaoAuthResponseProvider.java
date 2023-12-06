package edonymyeon.backend.member.auth.application;

import edonymyeon.backend.member.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.member.auth.application.dto.KakaoLoginResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoAuthResponseProvider {

    private static final String KAKAO_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoLoginResponse request(final KakaoLoginRequest loginRequest) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginRequest.accessToken());
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");
        final HttpEntity<Object> kakaoRequest = new HttpEntity<>(httpHeaders);

        return restTemplate.postForObject(KAKAO_INFO_REQUEST_URL, kakaoRequest, KakaoLoginResponse.class);
    }
}
