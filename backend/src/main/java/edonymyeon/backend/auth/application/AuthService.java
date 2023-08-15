package edonymyeon.backend.auth.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_DUPLICATE;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.domain.TokenGenerator;
import edonymyeon.backend.auth.domain.ValidateType;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.domain.Member.SocialType;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private static final String KAKAO_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    private final MemberRepository memberRepository;

    private final TokenGenerator tokenGenerator;

    public MemberId findMember(final LoginRequest loginRequest) {
        return findMember(loginRequest.email(), loginRequest.password());
    }

    //todo: 비밀번호까지 조회에 사용하나?
    public MemberId findMember(final String email, final String password) {
        final Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_EMAIL_NOT_FOUND));
        member.checkPassword(password);
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

    public KakaoLoginResponse getKakaoLoginResponse(final KakaoLoginRequest loginRequest)
            throws JsonProcessingException {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginRequest.accessToken());
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");
        final HttpEntity<Object> kakaoRequest = new HttpEntity<>(httpHeaders);

        final ResponseEntity<String> exchange = restTemplate.exchange(
                KAKAO_INFO_REQUEST_URL,
                HttpMethod.POST,
                kakaoRequest,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(exchange.getBody(), KakaoLoginResponse.class);
    }

    //todo session으로
    @Transactional
    public String findMemberByKakao(final KakaoLoginResponse kakaoLoginResponse) {
        final Member member = memberRepository.findBySocialIdAndSocialType(kakaoLoginResponse.id(), SocialType.KAKAO)
                .orElseGet(() -> joinSocialMember(kakaoLoginResponse.id(), SocialType.KAKAO));
        return tokenGenerator.getBasicToken(member.getEmail(), member.getPassword());
    }

    @Transactional
    public Member joinSocialMember(final Long socialId, final SocialType socialType) {
        final Member member = Member.of(socialId, socialType);
        return memberRepository.save(member);
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
