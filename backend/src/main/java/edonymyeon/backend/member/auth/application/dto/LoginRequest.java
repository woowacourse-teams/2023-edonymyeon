package edonymyeon.backend.member.auth.application.dto;

public record LoginRequest(String email, String password, String deviceToken) {

}
