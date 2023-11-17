package edonymyeon.backend.member.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.image.application.ImageService;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.request.MemberUpdateRequest;
import edonymyeon.backend.member.application.dto.response.MemberUpdateResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.support.IntegrationFixture;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class MemberServiceTest extends IntegrationFixture implements ImageFileCleaner {

    private final DeviceRepository deviceRepository;

    private final AuthService authService;

    private final MemberService memberService;

    @SpyBean
    private ImageService imageService;

    @Test
    void 하나의_디바이스는_하나의_계정에서만_활성화_되어야_한다_회원가입() {
        final String 동일한_디바이스_토큰 = "testDeviceToken";

        authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "nickname", 동일한_디바이스_토큰));
        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰)).isPresent();

        authService.joinMember(new JoinRequest("test2@gmail.com", "password123!", "nickname2", 동일한_디바이스_토큰));
        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰))
                .as("같은 디바이스가 여러 계정에서 활성화되어 있다면 NonUniqueResultException으로 인해 테스트가 실패해야 한다.")
                .isPresent();
    }

    @Test
    void 하나의_디바이스는_하나의_계정에서만_활성화_되어야_한다_로그인() {
        final String 동일한_디바이스_토큰 = "testDeviceToken";
        authService.joinMember(new JoinRequest("test@gmail.com", "password123!", "nickname", 동일한_디바이스_토큰));
        authService.joinMember(new JoinRequest("test2@gmail.com", "password123!", "nickname2", 동일한_디바이스_토큰));

        authService.login(new LoginRequest("test@gmail.com", "password123!", 동일한_디바이스_토큰));
        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰))
                .as("같은 디바이스가 여러 계정에서 활성화되어 있다면 NonUniqueResultException으로 인해 테스트가 실패해야 한다.")
                .isPresent();
    }

    @Test
    void 하나의_디바이스는_하나의_계정에서만_활성화_되어야_한다_소셜로그인() {
        final String 동일한_디바이스_토큰 = "testDeviceToken";
        authService.loginByKakao(new KakaoLoginResponse(1L), 동일한_디바이스_토큰);
        authService.loginByKakao(new KakaoLoginResponse(2L), 동일한_디바이스_토큰);

        assertThat(deviceRepository.findByDeviceTokenAndIsActiveIsTrue(동일한_디바이스_토큰))
                .as("같은 디바이스가 여러 계정에서 활성화되어 있다면 NonUniqueResultException으로 인해 테스트가 실패해야 한다.")
                .isPresent();
    }

    @Test
    void 닉네임만_변경되는_경우(@Autowired MemberRepository memberRepository) {
        final Member member = memberTestSupport.builder()
                .nickname("originalNickname")
                .build();
        final var newNickname = "newNickname";
        final MemberUpdateRequest updateRequest = new MemberUpdateRequest(newNickname, null, false);

        final MemberUpdateResponse response = memberService.updateMember(new ActiveMemberId(member.getId()),
                updateRequest);
        final Member updatedMember = memberRepository.findById(response.id()).orElseThrow();

        assertThat(updatedMember.getNickname()).isEqualTo(newNickname);
    }

    @Test
    void 프로필_이미지가_새로_등록되는_경우(@Autowired MemberRepository memberRepository) throws IOException {
        final Member member = memberTestSupport.builder()
                .build();
        final ProfileImageInfo originalProfileImage = member.getProfileImageInfo();

        final MockMultipartFile newImageFile = mockMultipartFileTestSupport.builder().buildImageForProfile();
        final MemberUpdateRequest updateRequest = new MemberUpdateRequest(member.getNickname(), newImageFile, true);

        final MemberUpdateResponse response = memberService.updateMember(new ActiveMemberId(member.getId()),
                updateRequest);
        final Member updatedMember = memberRepository.findById(response.id()).orElseThrow();

        assertSoftly(
                soft -> {
                    soft.assertThat(updatedMember.getNickname()).isEqualTo(updateRequest.nickname());
                    soft.assertThat(originalProfileImage).isNull();
                    soft.assertThat(updatedMember.getProfileImageInfo()).isNotNull();
                }
        );
    }

    @Test
    void 프로필_이미지가_삭제되는_경우(@Autowired MemberRepository memberRepository) throws IOException {
        final ProfileImageInfo profileImageInfo = profileImageInfoTestSupport.builder()
                .buildWithImageFile();
        final Member member = memberTestSupport.builder()
                .profileImageInfo(profileImageInfo)
                .build();

        final MemberUpdateRequest updateRequest = new MemberUpdateRequest(member.getNickname(), null, true);

        final MemberUpdateResponse response = memberService.updateMember(new ActiveMemberId(member.getId()),
                updateRequest);

        final Member updatedMember = memberRepository.findById(response.id()).orElseThrow();

        assertSoftly(
                soft -> {
                    soft.assertThat(updatedMember.getId()).isEqualTo(member.getId());
                    soft.assertThat(updatedMember.getNickname()).isEqualTo(updateRequest.nickname());
                    soft.assertThat(updatedMember.getProfileImageInfo()).isNull();
                    await().untilAsserted(() -> verify(imageService, atLeastOnce()).removeImage(any(), any()));
                }
        );
    }

    @Test
    void 프로필_이미지가_바뀌는_경우(@Autowired MemberRepository memberRepository) throws IOException {
        final ProfileImageInfo profileImageInfo = profileImageInfoTestSupport.builder()
                .buildWithImageFile();
        final Member member = memberTestSupport.builder()
                .profileImageInfo(profileImageInfo)
                .build();

        final MockMultipartFile imageFile = mockMultipartFileTestSupport.builder().buildImageForProfile();
        final MemberUpdateRequest updateRequest = new MemberUpdateRequest(member.getNickname(), imageFile, true);

        final MemberUpdateResponse response = memberService.updateMember(new ActiveMemberId(member.getId()),
                updateRequest);

        final Member updatedMember = memberRepository.findById(response.id()).orElseThrow();

        assertSoftly(
                soft -> {
                    soft.assertThat(updatedMember.getId()).isEqualTo(member.getId());
                    soft.assertThat(updatedMember.getNickname()).isEqualTo(updateRequest.nickname());
                    soft.assertThat(updatedMember.getProfileImageInfo()).isNotNull();
                    await().untilAsserted(() -> verify(imageService, atLeastOnce()).removeImage(any(), any()));
                    await().untilAsserted(() -> verify(imageService, atLeastOnce()).save(any(), any()));
                }
        );
    }

    @Test
    void 닉네임_프로필_이미지가_모두_변경되는_경우(@Autowired MemberRepository memberRepository) throws IOException {
        final ProfileImageInfo profileImageInfo = profileImageInfoTestSupport.builder()
                .buildWithImageFile();
        final Member member = memberTestSupport.builder()
                .profileImageInfo(profileImageInfo)
                .build();

        final MockMultipartFile imageFile = mockMultipartFileTestSupport.builder().buildImageForProfile();
        final MemberUpdateRequest updateRequest = new MemberUpdateRequest("newNickname", imageFile, true);

        final MemberUpdateResponse response = memberService.updateMember(new ActiveMemberId(member.getId()),
                updateRequest);

        final Member updatedMember = memberRepository.findById(response.id()).orElseThrow();

        assertSoftly(
                soft -> {
                    soft.assertThat(updatedMember.getId()).isEqualTo(member.getId());
                    soft.assertThat(updatedMember.getNickname()).isEqualTo(updateRequest.nickname());
                    soft.assertThat(updatedMember.getProfileImageInfo()).isNotNull();
                    await().untilAsserted(() -> verify(imageService, atLeastOnce()).removeImage(any(), any()));
                    await().untilAsserted(() -> verify(imageService, atLeastOnce()).save(any(), any()));
                }
        );
    }

    @Test
    void 실제_이미지_저장이_실패하면_회원정보_수정이_실패한다(@Autowired MemberRepository memberRepository) throws IOException {
        final ProfileImageInfo profileImageInfo = profileImageInfoTestSupport.builder()
                .buildWithImageFile();
        final Member member = memberTestSupport.builder()
                .profileImageInfo(profileImageInfo)
                .build();

        final MockMultipartFile imageFile = mockMultipartFileTestSupport.builder().buildImageForProfile();
        final MemberUpdateRequest updateRequest = new MemberUpdateRequest("newNickname", imageFile, true);

        doThrow(new RuntimeException("롤백용 예외")).when(imageService).save(any(), any());

        assertThatThrownBy(() -> memberService.updateMember(new ActiveMemberId(member.getId()),
                updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("롤백용 예외");

        final Member updatedMember = memberRepository.findById(member.getId()).orElseThrow();

        SoftAssertions.assertSoftly(
                soft -> {
                    verify(imageService, atLeastOnce()).save(any(), any());
                    soft.assertThat(updatedMember.getNickname()).isNotEqualTo(updateRequest.nickname());
                }
        );
    }

    @Test
    @DisplayName("동일한 디바이스 정보가 여러 회원에 걸쳐 존재할 때에도 로그아웃이 정상 동작해야 한다.")
    void deviceLogoutBug() {
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
