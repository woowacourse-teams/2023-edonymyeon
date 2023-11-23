package edonymyeon.backend.membber.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;

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
public final class Nickname {

    public static final String NONE = "Unknown";
    private static final int MAX_NICKNAME_LENGTH = 20;

    @Column(name = "nickname", nullable = false, unique = true)
    private String value;

    private Nickname(final String nickname) {
        this.value = nickname;
    }

    public static Nickname from(final String nickname) {
        validate(nickname);
        return new Nickname(nickname);
    }

    private static void validate(final String nickname) {
        if (Strings.isBlank(nickname) || nickname.length() > MAX_NICKNAME_LENGTH
                || nickname.equalsIgnoreCase(NONE)) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
    }

    public static Nickname from(final SocialType socialType) {
        return new Nickname("#" + socialType.name() + UUID.randomUUID());
    }

    public String getValue() {
        return value;
    }
}
