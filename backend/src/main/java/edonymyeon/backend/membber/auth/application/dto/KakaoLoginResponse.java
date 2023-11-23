package edonymyeon.backend.membber.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoLoginResponse(Long id) {

}
