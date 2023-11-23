package edonymyeon.backend.member.auth.domain;

public interface TokenGenerator {

    String getToken(final String email);
}
