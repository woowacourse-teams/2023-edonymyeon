package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_NOT_MATCH;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends TemporalRecord {

    private static final int MAX_EMAIL_LENGTH = 30;
    private static final int MAX_PASSWORD_LENGTH = 30;
    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final String UNKNOWN = "Unknown";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private ProfileImageInfo profileImageInfo;

    @ColumnDefault(value = "false")
    private boolean deleted = false;

    public Member(final String email, final String password, final String nickname,
                  final ProfileImageInfo profileImageInfo) {
        validate(email, password, nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageInfo = profileImageInfo;
    }

    public Member(final Long id) {
        this.id = id;
    }

    private void validate(final String email, final String password, final String nickname) {
        validateEmail(email);
        validateNickName(nickname);
        validatePassword(password);
    }

    private void validateEmail(final String email) {
        if (Objects.isNull(email) || email.isBlank() || email.length() > MAX_EMAIL_LENGTH) {
            throw new EdonymyeonException(MEMBER_EMAIL_INVALID);
        }
    }

    private void validateNickName(final String nickname) {
        if (Objects.isNull(nickname) || nickname.isBlank() || nickname.length() > MAX_NICKNAME_LENGTH
                || nickname.equalsIgnoreCase(UNKNOWN)) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
    }

    private void validatePassword(final String password) {
        if (PasswordValidator.isValidPassword(password)) {
            return;
        }
        throw new EdonymyeonException(MEMBER_PASSWORD_INVALID);
    }

    public void checkPassword(final String password) {
        if (this.password.equals(password)) {
            return;
        }
        throw new EdonymyeonException(MEMBER_PASSWORD_NOT_MATCH);
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getNickname() {
        if (deleted) {
            return UNKNOWN;
        }
        return nickname;
    }
}
