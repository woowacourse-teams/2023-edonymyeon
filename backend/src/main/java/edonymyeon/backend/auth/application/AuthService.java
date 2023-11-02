package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_DUPLICATE;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_NOT_MATCH;

import edonymyeon.backend.member.application.dto.response.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.application.event.JoinMemberEvent;
import edonymyeon.backend.auth.application.event.LoginEvent;
import edonymyeon.backend.auth.application.event.LogoutEvent;
import edonymyeon.backend.auth.domain.PasswordEncoder;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import edonymyeon.backend.member.domain.SocialInfo.SocialType;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final ApplicationEventPublisher publisher;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final MemberService memberService;

    /**
     * 일반적인 로그인 작업을 수행합니다.
     * @param loginRequest 로그인에 필요한 정보
     * @return 로그인한 사용자의 식별자
     */
    public MemberId login(final LoginRequest loginRequest) {
        final Member member = authenticateMember(loginRequest.email(), loginRequest.password());
        publisher.publishEvent(new LoginEvent(member, loginRequest.deviceToken()));
        return new ActiveMemberId(member.getId());
    }

    private Member authenticateMember(final String email, final String password) {
        final Member member = findByEmail(email);
        checkPassword(password, member.getPassword());
        return member;
    }

    private Member findByEmail(final String email) {
        final Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_EMAIL_NOT_FOUND));
        if (member.isDeleted()) {
            throw new EdonymyeonException(MEMBER_IS_DELETED);
        }
        return member;
    }

    private void checkPassword(final String rawPassword, final String encodedPassword) {
        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            return;
        }
        throw new EdonymyeonException(MEMBER_PASSWORD_NOT_MATCH);
    }

    /**
     * 가입하는 과정에서, 주어진 이메일 또는 닉네임이 이미 가입된 중복값인지 확인합니다.
     * @param target 확인하려는 값의 종류 (이메일, 닉네임 등)
     * @param value 확인하려는 값
     * @return 중복 여부
     */
    public DuplicateCheckResponse checkDuplicate(final String target, final String value) {
        return memberService.checkDuplicate(target, value);
    }

    /**
     * KAKAO로 로그인을 수행합니다.
     * @param kakaoLoginResponse KAKAO 로그인에 필요한 정보
     * @param deviceToken 로그인하는 디바이스의 토큰 값
     * @return 가입 완료된 회원의 정보
     */
    @Transactional
    public MemberId loginByKakao(final KakaoLoginResponse kakaoLoginResponse, final String deviceToken) {
        final SocialInfo socialInfo = SocialInfo.of(SocialType.KAKAO, kakaoLoginResponse.id());
        final Member member = memberRepository.findBySocialInfo(socialInfo)
                .orElseGet(() -> joinSocialMember(socialInfo, deviceToken));
        if (member.isDeleted()) {
            throw new EdonymyeonException(MEMBER_IS_DELETED);
        }

        publisher.publishEvent(new LoginEvent(member, deviceToken));
        return new ActiveMemberId(member.getId());
    }

    /**
     * 소셜 로그인 방식으로 회원가입을 합니다.
     *
     * @param socialInfo  소셜 회원가입에 필요한 정보
     * @param deviceToken 회원가입 시 사용하는 디바이스 정보
     * @return 가입 완료된 회원
     */
    @Transactional
    public Member joinSocialMember(final SocialInfo socialInfo, final String deviceToken) {
        final Member member = saveMember(Member.from(socialInfo));
        publisher.publishEvent(new JoinMemberEvent(member, deviceToken));
        return member;
    }

    private Member saveMember(Member member) {
        final String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.encrypt(encodedPassword);
        return memberRepository.save(member);
    }

    /**
     * 일반적인 방식으로 회원가입을 진행합니다.
     * @param joinRequest 회원가입에 필요한 정보
     * @return 가입 완료된 회원
     */
    @Transactional
    public Member joinMember(final JoinRequest joinRequest) {
        final Member member = new Member(
                joinRequest.email(),
                joinRequest.password(),
                joinRequest.nickname(),
                null,
                List.of(joinRequest.deviceToken())
        );

        validateDuplicateEmail(joinRequest.email());
        validateDuplicateNickname(joinRequest.nickname());

        final Member savedMember = saveMember(member);
        publisher.publishEvent(new JoinMemberEvent(savedMember, joinRequest.deviceToken()));
        return savedMember;
    }

    private void validateDuplicateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new EdonymyeonException(MEMBER_EMAIL_DUPLICATE);
        }
    }

    private void validateDuplicateNickname(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
    }

    /**
     * 로그아웃합니다.
     * @param deviceToken 로그아웃하는 디바이스의 토큰 값
     */
    @Transactional
    public void logout(String deviceToken) {
        publisher.publishEvent(new LogoutEvent(deviceToken));
    }

    @Transactional
    public void withdraw(final MemberId memberId) {
        final Member member = memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
        memberService.deleteProfileImage(member);
        member.withdraw();
    }
}
