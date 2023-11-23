package edonymyeon.backend.membber.auth.domain;

public interface TokenGenerator {

    String getToken(final String email);
}
