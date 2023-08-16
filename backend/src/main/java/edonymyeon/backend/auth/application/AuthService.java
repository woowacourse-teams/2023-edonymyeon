package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_DUPLICATE;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_IS_DELETED;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;

import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    public MemberId findMember(final LoginRequest loginRequest) {
        return findMember(loginRequest.email(), loginRequest.password());
    }

    //todo: 비밀번호까지 조회에 사용하나?
    public MemberId findMember(final String email, final String password) {
        final Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_EMAIL_NOT_FOUND));
        member.checkPassword(password);
        if (member.isDeleted()) {
            throw new EdonymyeonException(MEMBER_IS_DELETED);
        }
        return new ActiveMemberId(member.getId());
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

    // todo: 비밀번호 암호화
    @Transactional
    public void joinMember(final JoinRequest joinRequest) {
        final Member member = new Member(
                joinRequest.email(),
                joinRequest.password(),
                joinRequest.nickname(),
                null
        );

        validateDuplicateEmail(joinRequest.email());
        validateDuplicateNickname(joinRequest.nickname());

        memberRepository.save(member);
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
}
