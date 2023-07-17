package edonymyeon.backend.service;

import static edonymyeon.backend.domain.exception.ExceptionCode.MEMBER_EMAIL_NOT_FOUND;

import edonymyeon.backend.domain.Member;
import edonymyeon.backend.domain.exception.EdonymyeonException;
import edonymyeon.backend.repository.MemberRepository;
import edonymyeon.backend.service.request.LoginRequest;
import edonymyeon.backend.service.response.MemberIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

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
