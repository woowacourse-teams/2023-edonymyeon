package edonymyeon.backend.auth.application.dto;

public record JoinRequest(String email, String password, String nickname, String deviceToken) {

}
