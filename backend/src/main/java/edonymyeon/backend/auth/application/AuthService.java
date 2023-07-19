package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;

import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    //todo: 비밀번호까지 조회에 사용하나?
    public MemberIdDto findMember(final String email, final String password) {
        final Member member = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_EMAIL_NOT_FOUND));
        return new MemberIdDto(member.getId());
    }

    public MemberIdDto findMember(final LoginRequest loginRequest) {
        return findMember(loginRequest.email(), loginRequest.password());
    }
}
