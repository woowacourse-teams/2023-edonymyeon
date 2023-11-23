package edonymyeon.backend.membber.auth.application.dto;

public record LoginRequest(String email, String password, String deviceToken) {

}
