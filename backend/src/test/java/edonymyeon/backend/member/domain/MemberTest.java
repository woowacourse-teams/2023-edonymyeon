package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
    public void 비밀번호가_8자_미만이면_예외발생() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            password.append(i);
        }
        final String invalidPassword = password.toString();
        assertThatThrownBy(() -> new Member("email", invalidPassword, "nickname", null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
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
    public void 비밀번호가_영문_숫자_특수문자가_포함되지_않으면_예외발생() {
        final String invalidPassword1 = "abcd1234";
        final String invalidPassword2 = "!@#1234";
        final String invalidPassword3 = "abcd!@#";

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThatThrownBy(() -> new Member("email", invalidPassword1, "nickname", null));
                    softAssertions.assertThatThrownBy(() -> new Member("email", invalidPassword2, "nickname", null));
                    softAssertions.assertThatThrownBy(() -> new Member("email", invalidPassword3, "nickname", null));
                }
        );
    }

    @Test
    public void 닉네임이_20자_초과이면_예외발생() {
        StringBuilder nickName = new StringBuilder();
        for (int i = 0; i < 21; i++) {
            nickName.append(i);
        }
        final String invalidNickName = nickName.toString();
        assertThatThrownBy(() -> new Member("email", "password", invalidNickName, null))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }
}
