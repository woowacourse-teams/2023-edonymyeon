package edonymyeon.backend.auth.application;

import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.domain.PasswordEncoder;
import edonymyeon.backend.image.application.ImageService;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.member.application.DeviceRepository;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.domain.Device;
import edonymyeon.backend.member.domain.Email;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import edonymyeon.backend.member.domain.SocialInfo.SocialType;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.support.IntegrationTest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@IntegrationTest
class AuthServiceTest {

    @SpyBean
    private SettingService settingService;

    @SpyBean
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private MemberRepository memberRepository;

    @SpyBean
    private ImageService imageService;

    @Test
    void 회원가입시_사용한_디바이스_정보를_함께_저장한다(@Autowired EntityManager entityManager) {
        doNothing().when(settingService).initializeSettings(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        final Member member = entityManager.createQuery(
                        "select m from Member m left join fetch m.devices where m.email = :email", Member.class)
                .setParameter("email", Email.from("test@gmail.com"))
                .getSingleResult();
        assertThat(member.getDevices()).hasSize(1);
    }

    @Test
    void 최초의_소셜로그인시_사용한_디바이스_정보를_함께_저장한다(@Autowired EntityManager entityManager) {
        doNothing().when(settingService).initializeSettings(any());

        authService.loginByKakao(new KakaoLoginResponse(1L), "testDeviceToken");

        final Member member = entityManager.createQuery(
                        "select m from Member m left join fetch m.devices where m.socialInfo.socialId = :socialId", Member.class)
                .setParameter("socialId", 1L)
                .getSingleResult();
        assertThat(member.getDevices()).hasSize(1);
    }

    @Test
    void 회원가입_이후_설정초기화_작업을_수행한다() {
        doNothing().when(settingService).initializeSettings(any());

        authService.joinMember(
                new JoinRequest("test@gmail.com", "@testPassword234", "testNickname", "testDeviceToken"));
        verify(settingService, atLeastOnce()).initializeSettings(any());
    }

    @Test
    void 소셜_회원가입_이후_설정초기화_작업을_수행한다() {
        doNothing().when(settingService).initializeSettings(any());

        authService.joinSocialMember(SocialInfo.of(SocialType.KAKAO, 1L), "testDeviceToken");
        verify(settingService, atLeastOnce()).initializeSettings(any());
    }

    @Test
    void 로그인_이후_디바이스_교체_작업을_수행한다(@Autowired DeviceRepository deviceRepository) {
        //given
        final JoinRequest joinRequest = new JoinRequest("test@gmail.com", "@testPassword234", "testNickname",
                "testDeviceToken");
        authService.joinMember(joinRequest);

        //when
        final LoginRequest loginRequest = new LoginRequest("test@gmail.com", "@testPassword234", "testToken");
        authService.login(loginRequest);

        //then
        assertSoftly(
                softAssertions -> {
                    final List<Device> originalDevice = deviceRepository.findAllByDeviceToken(joinRequest.deviceToken());
                    final List<Device> changedDevice = deviceRepository.findAllByDeviceToken(loginRequest.deviceToken());
                    assertThat(changedDevice).hasSize(1);
                    assertThat(changedDevice.get(0).isActive()).isTrue();
                    assertThat(originalDevice.get(0).isActive()).isFalse();
                }
        );
    }

    @Test
    void 소셜로그인_이후에도_디바이스_교체_작업을_수행한다(@Autowired DeviceRepository deviceRepository) {
        //given
        final String originalDeviceToken = "testDeviceToken";
        authService.loginByKakao(new KakaoLoginResponse(1L), originalDeviceToken);

        //when
        final String changedDeviceToken = "testDeviceToken2";
        authService.loginByKakao(new KakaoLoginResponse(1L), changedDeviceToken);

        //then
        assertSoftly(
                softAssertions -> {
                    final List<Device> originalDevice = deviceRepository.findAllByDeviceToken(originalDeviceToken);
                    final List<Device> changedDevice = deviceRepository.findAllByDeviceToken(changedDeviceToken);
                    assertThat(changedDevice).hasSize(1);
                    assertThat(changedDevice.get(0).isActive()).isTrue();
                    assertThat(originalDevice.get(0).isActive()).isFalse();
                }
        );
    }

    @Test
    void 기본_회원가입시_DB에_암호화된_비밀번호가_저장된다() {
        final JoinRequest request = new JoinRequest("example@email.com", "password1234!", "nickname", "");
        final Member member = authService.joinMember(request);

        assertSoftly(soft -> {
            soft.assertThat(request.email()).isEqualTo(member.getEmail());
            soft.assertThat(request.password()).isNotEqualTo(member.getPassword());
            verify(passwordEncoder, atLeastOnce()).encode(any());
        });
    }

    @Test
    void 카카오_회원가입시_DB에_암호화된_비밀번호가_저장된다() {
        final SocialInfo socialInfo = SocialInfo.of(SocialInfo.SocialType.KAKAO, 1L);
        authService.joinSocialMember(socialInfo, "testDevice");

        verify(passwordEncoder, atLeastOnce()).encode(any());
    }

    @Test
    void 기본_로그인시_DB에_암호화된_비밀번호와_입력받은_비밀번호를_검증한다() {
        final JoinRequest request = new JoinRequest("example@email.com", "password1234!", "nickname", "");
        authService.joinMember(request);

        final LoginRequest loginRequest = new LoginRequest(request.email(), request.password(), "");

        assertSoftly(soft -> {
            assertDoesNotThrow(() -> authService.login(loginRequest));
            verify(passwordEncoder, atLeastOnce()).encode(any());
            verify(passwordEncoder, atLeastOnce()).matches(any(), any());
        });
    }

    @Test
    void 회원_삭제의_트랜잭션이_커밋되면_프로필_이미지를_물리적으로_삭제한다() {
        // given
        final JoinRequest request = new JoinRequest("example@email.com", "password1234!", "nickname", "");
        final Member member = authService.joinMember(request);

        final Member mockedMember = mock(Member.class);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(mockedMember));
        final ImageInfo imageInfo = new ImageInfo("storeName");
        when(mockedMember.getProfileImageInfo()).thenReturn(ProfileImageInfo.from(imageInfo));

        // when
        final ActiveMemberId memberId = new ActiveMemberId(member.getId());
        authService.withdraw(memberId);

        // then
        SoftAssertions.assertSoftly(
                soft -> {
                    verify(memberService, atLeastOnce()).deleteProfileImage(mockedMember);
                    await().untilAsserted(() -> verify(imageService, atLeastOnce()).removeImage(any(), any()));
                }
        );
    }

    @Test
    void 회원_삭제의_트랜잭션이_롤백되면_프로필_이미지를_물리적으로_삭제하지않는다() {
        // given
        final var request = new JoinRequest("example@email.com", "password1234!", "nickname", "");
        final var member = authService.joinMember(request);

        final var mockedMember = mock(Member.class);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(mockedMember));
        final ImageInfo imageInfo = new ImageInfo("storeName");
        when(mockedMember.getProfileImageInfo()).thenReturn(ProfileImageInfo.from(imageInfo));
        doThrow(new RuntimeException("롤백용 예외")).when(mockedMember).withdraw();

        // when
        final ActiveMemberId memberId = new ActiveMemberId(member.getId());

        // then
        SoftAssertions.assertSoftly(
                soft -> {
                    soft.assertThatThrownBy(() -> authService.withdraw(memberId));
                    verify(memberService, atLeastOnce()).deleteProfileImage(mockedMember);
                    verify(imageService, never()).removeImage(any(), any());
                }
        );
    }

    @Test
    void 동일한_디바이스_정보가_여러_회원에_걸쳐_존재할_때에도_로그아웃이_정상_동작해야_한다() {
        // Given : 회원이 새로 가입하여 로그인한 후 로그아웃한다.
        authService.joinMember(new JoinRequest(("test@gmail.com"), "password123!", "nickname", "sameDevice"));
        authService.login(new LoginRequest("test@gmail.com", "password123!", "sameDevice"));
        authService.logout("sameDevice");

        // When : 동일한 디바이스를 가지고 새로운 회원이 가입하여 로그인한 후 로그아웃한다.
        authService.joinMember(new JoinRequest(("test2@gmail.com"), "password123!", "nickname2", "sameDevice"));
        authService.login(new LoginRequest("test2@gmail.com", "password123!", "sameDevice"));
        assertThatCode(() -> authService.logout("sameDevice"))
                .doesNotThrowAnyException();
    }
}
