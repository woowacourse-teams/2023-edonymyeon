package edonymyeon.backend.member.profile.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static edonymyeon.backend.member.profile.domain.SocialInfo.SocialType;

import edonymyeon.backend.member.auth.domain.PasswordEncoder;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password from(final String password) {
        validate(password);
        return new Password(password);
    }

    private static void validate(final String password) {
        if (PasswordValidator.isValidPassword(password)) {
            return;
        }
        throw new EdonymyeonException(MEMBER_PASSWORD_INVALID);
    }

    public static Password from(final SocialType socialType) {
        final String uuid = "#" + socialType.name() + UUID.randomUUID();
        final String defaultPassword = uuid.replace("-", "").substring(0, 25) + "!";
        return new Password(defaultPassword);
    }

    public Password encrypt(final PasswordEncoder passwordEncoder) {
        if (PasswordValidator.isEncodedPassword(value)) {
            throw new BusinessLogicException(ExceptionInformation.ALREADY_ENCODED_PASSWORD);
        }
        return new Password(passwordEncoder.encode(value));
    }

    public String getValue() {
        return value;
    }
}
