package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    private static final int MAX_EMAIL_LENGTH = 30;
    private static final int MAX_PASSWORD_LENGTH = 30;
    private static final int MAX_NICKNAME_LENGTH = 20;

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

    public Member(final String email, final String password, final String nickname,
                  final ProfileImageInfo profileImageInfo) {
        validate(email, password, nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageInfo = profileImageInfo;
    }

    private void validate(final String email, final String password, final String nickname) {
        validateEmail(email);
        validatePassword(password);
        validateNickName(nickname);
    }

    private void validateEmail(final String email) {
        if (Objects.isNull(email) || email.length() > MAX_EMAIL_LENGTH) {
            throw new EdonymyeonException(MEMBER_EMAIL_INVALID);
        }
    }

    private void validatePassword(final String password) {
        if (Objects.isNull(password) || password.length() > MAX_PASSWORD_LENGTH) {
            throw new EdonymyeonException(MEMBER_PASSWORD_INVALID);
        }
    }

    private void validateNickName(final String nickname) {
        if (Objects.isNull(nickname) || nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
    }
}
