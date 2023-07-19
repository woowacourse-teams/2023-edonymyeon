package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberTest {

    @Test
    public void 이메일이_30자_초과이면_예외발생() {
        StringBuilder email = new StringBuilder();
        for (int i = 0; i < 31; i++) {
            email.append(i);
        }
        final String invalidEmail = email.toString();
        assertThatThrownBy(() -> new Member(invalidEmail, "password", "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }

    @Test
    public void 비밀번호가_30자_초과이면_예외발생() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 31; i++) {
            password.append(i);
        }
        final String invalidPassword = password.toString();
        assertThatThrownBy(() -> new Member("email", invalidPassword, "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    public void 닉네임이_10자_초과이면_예외발생() {
        StringBuilder nickName = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            nickName.append(i);
        }
        final String invalidNickName = nickName.toString();
        assertThatThrownBy(() -> new Member("email", "password", invalidNickName, null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }
}
