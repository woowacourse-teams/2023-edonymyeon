package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void 멤버_삭제후_조회시_예외반환() {
        final Member member = Member.builder()
                .email("test@email.com")
                .password("passwrod1234!")
                .deleted(true)
                .build();
        when(memberRepository.findByEmail(member.getEmail()))
                .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> authService.findMember(member.getEmail(), member.getPassword()))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(MEMBER_IS_DELETED.getMessage());
    }
}
