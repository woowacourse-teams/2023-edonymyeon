package edonymyeon.backend.membber.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.membber.member.domain.SocialInfo.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {

    private static final int MAX_EMAIL_LENGTH = 30;

    @Column(name = "email", nullable = false, unique = true)
    private String value;

    private Email(String email) {
        this.value = email;
    }

    public static Email from(String email) {
        validate(email);
        return new Email(email);
    }

    private static void validate(final String email) {
        if (Strings.isBlank(email) || email.length() > MAX_EMAIL_LENGTH) {
            throw new EdonymyeonException(MEMBER_EMAIL_INVALID);
        }
    }

    public static Email from(final SocialType socialType) {
        return new Email("#" + socialType.name() + UUID.randomUUID());
    }

    public String getValue() {
        return value;
    }
}
