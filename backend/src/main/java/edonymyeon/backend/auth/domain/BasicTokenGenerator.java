package edonymyeon.backend.auth.domain;

import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class BasicTokenGenerator implements TokenGenerator {

    @Override
    public String getToken(final String email, final String password) {
        String valueToEncode = email + ":" + password;

        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
