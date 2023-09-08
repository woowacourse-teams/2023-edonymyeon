package edonymyeon.backend.member.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class PasswordValidator {

    private PasswordValidator() {
    }

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$";
    private static final String ENCODED_PATTERN = "^\\$.+\\$.+\\$.+";

    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private static final Pattern encodedPattern = Pattern.compile(ENCODED_PATTERN);

    public static boolean isValidPassword(String password) {
        if (Objects.isNull(password) || password.isBlank()) {
            return false;
        }
        return passwordPattern.matcher(password).matches();
    }

    public static boolean isValidEncodedPassword(String encodedPassword) {
        if (Objects.isNull(encodedPassword) || encodedPassword.isBlank()) {
            return false;
        }
        return encodedPattern.matcher(encodedPassword).matches();
    }
}
