package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.TestMemberBuilder;
import edonymyeon.backend.support.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@IntegrationTest
public class AuthDeleteServiceTest {

    private final TestMemberBuilder testMemberBuilder;

    @Autowired
    private AuthService authService;

    @Test
    void 멤버_삭제후_조회시_예외반환() {
        final Member member = testMemberBuilder
                .builder()
                .id(1L)
                .email("test@email.com")
                .password("passwrod1234")
                .nickname("null")
                .deleted(true)
                .build();

        assertThatThrownBy(() -> authService.login(member.getEmail(), member.getPassword()))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_IS_DELETED.getMessage());
    }
}
