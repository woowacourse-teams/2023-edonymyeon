package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_DUPLICATE;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_NOT_MATCH;

import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.application.dto.MemberResponse;
import edonymyeon.backend.auth.application.event.JoinMemberEvent;
import edonymyeon.backend.auth.application.event.LoginEvent;
import edonymyeon.backend.auth.application.event.LogoutEvent;
import edonymyeon.backend.auth.domain.PasswordEncoder;
import edonymyeon.backend.auth.domain.ValidateType;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.SocialInfo;
import edonymyeon.backend.member.domain.SocialInfo.SocialType;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    public MemberId getAuthenticatedUser(final String email) {
        final Member member = findByEmail(email);
        return new ActiveMemberId(member.getId());
    }

    @NotNull
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


    public DuplicateCheckResponse checkDuplicate(final String target, final String value) {
        final ValidateType validateType = ValidateType.from(target);

        if (findMemberByValidateType(validateType, value).isEmpty()) {
            return new DuplicateCheckResponse(true);
        }
        return new DuplicateCheckResponse(false);
    }

    private Optional<Member> findMemberByValidateType(final ValidateType validateType, final String value) {
        if (validateType.equals(ValidateType.EMAIL)) {
            return memberRepository.findByEmail(value);
        }
        return memberRepository.findByNickname(value);
    }

    //todo session으로
    @Transactional
    public MemberResponse loginByKakao(final KakaoLoginResponse kakaoLoginResponse, final String deviceToken) {
        final SocialInfo socialInfo = SocialInfo.of(SocialType.KAKAO, kakaoLoginResponse.id());
        final Member member = memberRepository.findBySocialInfo(socialInfo)
                .orElseGet(() -> joinSocialMember(socialInfo));
        if (member.isDeleted()) {
            throw new EdonymyeonException(MEMBER_IS_DELETED);
        }

        publisher.publishEvent(new LoginEvent(member, deviceToken));
        return new MemberResponse(member.getEmail(), member.getPassword());
    }

    @Transactional
    public Member joinSocialMember(final SocialInfo socialInfo) {
        final Member member = Member.from(socialInfo);
        return saveMember(member);
    }

    private Member saveMember(Member member) {
        final String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.encrypt(encodedPassword);
        return memberRepository.save(member);
    }

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

        publisher.publishEvent(new JoinMemberEvent(member));
        return saveMember(member);
    }

    // todo DB에 암호화 적용하고 삭제
    @Transactional
    public void updatePasswordToEncrypt() {
        final List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            final String encodedPassword = passwordEncoder.encode(member.getPassword());
            try {
                member.encrypt(encodedPassword);
            } catch (EdonymyeonException e) {
            }
        }
    }

    private void validateDuplicateEmail(final String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new EdonymyeonException(MEMBER_EMAIL_DUPLICATE);
        }
    }

    private void validateDuplicateNickname(final String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
    }

    public void logout(String deviceToken) {
        publisher.publishEvent(new LogoutEvent(deviceToken));
    }
}
