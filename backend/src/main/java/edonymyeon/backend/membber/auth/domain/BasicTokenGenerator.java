package edonymyeon.backend.membber.auth.domain;

import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class BasicTokenGenerator implements TokenGenerator {

    @Override
    public String getToken(final String email) {
        String valueToEncode = email + ":";

        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
