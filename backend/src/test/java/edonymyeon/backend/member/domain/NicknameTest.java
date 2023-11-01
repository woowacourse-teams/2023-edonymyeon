package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.*;
import static edonymyeon.backend.member.domain.SocialInfo.SocialType;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import edonymyeon.backend.global.exception.EdonymyeonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class NicknameTest {

    @Test
    void 닉네임을_String으로_생성한다() {
        //given
        final String nickname = "newNickname";

        //when
        //then
        Assertions.assertDoesNotThrow(() -> Nickname.from(nickname));
    }

    @Test
    void 닉네임을_SocialType으로_생성한다() {
        //given
        final SocialType socialType = SocialType.KAKAO;

        //when
        //then
        Assertions.assertDoesNotThrow(() -> Nickname.from(socialType));
    }

    @Test
    void 닉네임이_20글자를_초과하면_예외가_발생한다() {
        //given
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 21; i++) {
            stringBuilder.append(i);
        }
        final String nickname = stringBuilder.toString();

        //when
        //then
        assertThatThrownBy(() -> Nickname.from(nickname))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_NICKNAME_INVALID.getMessage());
    }
}
