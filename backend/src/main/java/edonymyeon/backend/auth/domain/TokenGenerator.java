package edonymyeon.backend.auth.domain;

public interface TokenGenerator {

    String getToken(final String email);
}
