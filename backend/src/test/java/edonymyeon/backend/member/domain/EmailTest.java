package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class EmailTest {

    @Test
    void 이메일을_String으로_생성한다() {
        //given
        final String email = "email@test.com";

        //when
        //then
        Assertions.assertDoesNotThrow(() -> Email.from(email));
    }

    @Test
    void 이메일을_SocialType으로_생성한다() {
        //given
        final SocialInfo.SocialType socialType = SocialInfo.SocialType.KAKAO;

        //when
        //then
        Assertions.assertDoesNotThrow(() -> Email.from(socialType));
    }

    @Test
    void 이메일이_30글자를_초과하면_예외가_발생한다() {
        //given
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 31; i++) {
            stringBuilder.append(i);
        }
        final String email = stringBuilder.toString();

        //when
        //then
        assertThatThrownBy(() -> Email.from(email))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_EMAIL_INVALID.getMessage());
    }
}
