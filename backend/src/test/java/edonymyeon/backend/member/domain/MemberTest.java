package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberTest {

    @Test
    public void 이메일이_30자_초과이면_예외발생() {
        final String invalidEmail = "i".repeat(31);
        assertThatThrownBy(() -> new Member(invalidEmail, "password", "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }

    @Test
    public void 이메일이_공백이면_예외발생() {
        final String invalidEmail = "   ";
        assertThatThrownBy(() -> new Member(invalidEmail, "password", "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }

    @Test
    public void 비밀번호가_8자_미만이면_예외발생() {
        final String invalidPassword = "i".repeat(7);
        assertThatThrownBy(() -> new Member("email", invalidPassword, "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    public void 비밀번호가_30자_초과이면_예외발생() {
        final String invalidPassword = "i".repeat(31);
        assertThatThrownBy(() -> new Member("email", invalidPassword, "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @ValueSource(strings = {"abcd1234", "!@#1234", "abcd!@#"})
    @ParameterizedTest(name = "{index}. password: {0}")
    public void 비밀번호가_영문_숫자_특수문자가_포함되지_않으면_예외발생(String invalidPassword) {
        Assertions.assertThatThrownBy(() -> new Member("email", invalidPassword, "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    public void 닉네임이_20자_초과이면_예외발생() {
        final String invalidNickname = "i".repeat(21);
        assertThatThrownBy(() -> new Member("email", "password", invalidNickname, null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }

    @Test
    public void 닉네임이_공백이면_예외발생() {
        final String invalidNickname = "   ";
        assertThatThrownBy(() -> new Member(invalidNickname, "password", "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }
}
