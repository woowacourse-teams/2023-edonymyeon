package edonymyeon.backend.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("비밀번호 암호화 테스트")
class SimplePasswordEncoderTest {

    private final SimplePasswordEncoder encoder = new SimplePasswordEncoder();

    @Test
    void 같은_비밀번호를_암호화하더라도_다른_인코딩값을_얻는다() {
        //given
        final String password = "abc12345678!";

        //when
        final String encodedPassword1 = encoder.encode(password);
        final String encodedPassword2 = encoder.encode(password);

        //then
        assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
    }

    @Test
    void 인코딩된_비밀번호와_비밀번호_평문이_일치하는지_확인한다() {
        //given
        final String password = "abc12345678!";
        final String encodedPassword = encoder.encode(password);

        //when
        final boolean matches = encoder.matches(password, encodedPassword);

        //then
        assertThat(matches).isTrue();
    }
}
