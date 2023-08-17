package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.preference.application.PreferenceService;
import edonymyeon.backend.support.IntegrationTest;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@IntegrationTest
class AuthServiceTest {

    @SpyBean
    private PreferenceService preferenceService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입시_사용한_디바이스_정보를_함께_저장한다(@Autowired EntityManager entityManager) {
        doNothing().when(preferenceService).initializeMemberPreference(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        final Member member = entityManager.createQuery(
                        "select m from Member m left join fetch m.devices where m.email = :email", Member.class)
                .setParameter("email", "test@gmail.com")
                .getSingleResult();
        assertThat(member.getDevices()).hasSize(1);
    }

    @Test
    void 회원가입_이후_설정초기화_작업을_수행한다() {
        doNothing().when(preferenceService).initializeMemberPreference(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        verify(preferenceService, atLeastOnce()).initializeMemberPreference(any());
    }

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
