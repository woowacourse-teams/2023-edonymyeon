package edonymyeon.backend.member.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.member.auth.application.dto.LoginRequest;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.TestMemberBuilder;
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
class AuthDeleteServiceTest {

    private final TestMemberBuilder testMemberBuilder;

    @Autowired
    private AuthService authService;

    @Test
    void 멤버_삭제후_조회시_예외반환() {
        final Member member = testMemberBuilder
                .builder()
                .id(1L)
                .email("test@email.com")
                .nickname("null")
                .deleted(true)
                .build();

        final LoginRequest request = new LoginRequest(member.getEmail(), TestMemberBuilder.getRawPassword(),
                "");
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_IS_DELETED.getMessage());
    }
}
