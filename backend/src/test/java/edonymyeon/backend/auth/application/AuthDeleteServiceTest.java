package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.support.IntegrationTest;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ReflectionUtils;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@IntegrationTest
class AuthDeleteServiceTest {

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private AuthService authService;

    @Test
    void 멤버_삭제후_조회시_예외반환() {
        final Member member = new Member("test@email.com", "passwrod1234!", "null", null, List.of());

        final Field deletedField = ReflectionUtils.findField(Member.class, "deleted");
        ReflectionUtils.makeAccessible(deletedField);
        ReflectionUtils.setField(deletedField, member, true);

        when(memberRepository.findByEmail(member.getEmail()))
                .thenReturn(Optional.of(member));

        final String email = member.getEmail();
        assertThatThrownBy(() -> authService.getAuthenticatedUser(email))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_IS_DELETED.getMessage());
    }
}
