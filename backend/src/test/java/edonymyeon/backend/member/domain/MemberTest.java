package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.util.Collections;
import java.util.List;
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
    void 이메일이_30자_초과이면_예외발생() {
        final String invalidEmail = "i".repeat(31);
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> new Member(invalidEmail, "password1234!", "nickname", null, emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }

    @Test
    void 이메일이_공백이면_예외발생() {
        final String invalidEmail = "   ";
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> new Member(invalidEmail, "password1234!", "nickname", null, emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }

    @Test
    void 비밀번호가_8자_미만이면_예외발생() {
        final String invalidPassword = "i".repeat(7);
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> new Member("test@email.com", invalidPassword, "nickname", null, emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    void 비밀번호가_30자_초과이면_예외발생() {
        final String invalidPassword = "i".repeat(31);
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> new Member("test@email.com", invalidPassword, "nickname", null, emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @ValueSource(strings = {"abcd1234", "!@#1234", "abcd!@#"})
    @ParameterizedTest(name = "{index}. password: {0}")
    void 비밀번호가_영문_숫자_특수문자가_포함되지_않으면_예외발생(String invalidPassword) {
        List<String> emptyList = Collections.emptyList();
        Assertions.assertThatThrownBy(() -> new Member("test@email.com", invalidPassword, "nickname", null,
                        emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    void 닉네임이_20자_초과이면_예외발생() {
        final String invalidNickname = "i".repeat(21);
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> new Member("test@email.com", "password1234!", invalidNickname, null,
                emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }

    @Test
    void 닉네임이_공백이면_예외발생() {
        final String invalidNickname = "   ";
        List<String> emptyList = Collections.emptyList();
        assertThatThrownBy(() -> new Member("test@email.com", "password1234!", invalidNickname, null,
                emptyList))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }

    @Test
    void 삭제하면_닉네임은_Unknown() {
        final Member member = new Member("test@email.com", "password1234!", "nickname", null, Collections.emptyList());
        final String previousNickname = member.getNickname();
        member.withdraw();
        Assertions.assertThat(member.getNickname()).isNotEqualTo(previousNickname);
    }

    @Test
    void 회원정보_수정시_잘못된_닉네임_들어오면_예외발생() {
        List<String> emptyList = Collections.emptyList();
        final Member member = new Member("test@email.com", "password1234!", "nickname", null, emptyList);

        assertThatThrownBy(() -> member.updateNickname(""))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }
}
