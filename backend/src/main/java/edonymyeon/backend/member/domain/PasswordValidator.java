package edonymyeon.backend.member.domain;

import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;

public class PasswordValidator {

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$";
    private static final String ENCODED_PATTERN = "^\\$.+\\$.+\\$.{64,}$";

    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private static final Pattern encodedPattern = Pattern.compile(ENCODED_PATTERN);

    private PasswordValidator() {
    }

    public static boolean isValidPassword(String password) {
        if (Objects.isNull(password) || password.isBlank()) {
            return false;
        }
        return passwordPattern.matcher(password).matches();
    }

    public static boolean isEncodedPassword(String encodedPassword) {
        if (Strings.isBlank(encodedPassword)) {
            return false;
        }
        return encodedPattern.matcher(encodedPassword).matches();
    }
}
