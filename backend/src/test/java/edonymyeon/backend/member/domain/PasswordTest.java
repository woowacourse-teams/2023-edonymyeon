package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class PasswordTest {

    @Test
    void 비밀번호를_String으로_생성한다() {
        //given
        final String password = "pass1234!";

        //when
        //then
        Assertions.assertDoesNotThrow(() -> Password.from(password));
    }

    @Test
    void 비밀번호를_SocialType으로_생성한다() {
        //given
        final SocialInfo.SocialType socialType = SocialInfo.SocialType.KAKAO;

        //when
        //then
        Assertions.assertDoesNotThrow(() -> Password.from(socialType));
    }

    @Test
    void 비밀번호가_8글자를_미만이면_예외가_발생한다() {
        //given
        final String password = "pass12!";

        //when
        //then
        assertThatThrownBy(() -> Password.from(password))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    void 비밀번호가_30글자를_초과하면_예외가_발생한다() {
        //given
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 31; i++) {
            stringBuilder.append(i);
        }
        final String password = stringBuilder.toString();

        //when
        //then
        assertThatThrownBy(() -> Password.from(password))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }

    @Test
    void 비밀번호가_형식이_맞지않으면_예외가_발생한다() {
        //given
        final String password = "password;%/?";

        //when
        //then
        assertThatThrownBy(() -> Password.from(password))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_PASSWORD_INVALID.getMessage());
    }
}
