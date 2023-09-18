package edonymyeon.backend.auth.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.KakaoAuthResponseProvider;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.application.dto.LogoutRequest;
import edonymyeon.backend.auth.application.dto.MemberResponse;
import edonymyeon.backend.auth.domain.TokenGenerator;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.MemberId;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    private final KakaoAuthResponseProvider kakaoAuthResponseProvider;

    private final TokenGenerator tokenGenerator;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        authService.login(loginRequest);
        final String basicToken = tokenGenerator.getToken(loginRequest.email());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, basicToken)
                .build();
    }

    @PostMapping("/auth/kakao/login")
    public ResponseEntity<Void> loginWithKakao(@RequestBody KakaoLoginRequest loginRequest) {
        final KakaoLoginResponse kakaoLoginResponse = kakaoAuthResponseProvider.request(loginRequest);

        final MemberResponse memberResponse = authService.loginByKakao(kakaoLoginResponse, loginRequest.deviceToken());
        final String basicToken = tokenGenerator.getToken(memberResponse.email());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, basicToken)
                .build();
    }

    @GetMapping("/join")
    public ResponseEntity<DuplicateCheckResponse> validateDuplicate(@RequestParam String target,
                                                                    @RequestParam String value) {
        final DuplicateCheckResponse duplicateCheckResponse = authService.checkDuplicate(target, value);
        return ResponseEntity.ok().body(duplicateCheckResponse);
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody JoinRequest joinRequest) {
        authService.joinMember(joinRequest);
        return ResponseEntity.created(URI.create("/login")).build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthPrincipal MemberId memberId) {
        authService.withdraw(memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request) {
        try {
            request.logout();
        } catch (ServletException e) {
            log.error("로그아웃 실패", e);
            throw new BusinessLogicException(ExceptionInformation.LOGOUT_FAILED);
        }

        authService.logout(logoutRequest.deviceToken());
        return ResponseEntity.ok().build();
    }

    // todo DB에 비밀번호 암호화 후 삭제
    @PutMapping("/auth/encrypt")
    public ResponseEntity<Void> encryptAllMember() {
        authService.updatePasswordToEncrypt();
        return ResponseEntity.ok().build();
    }
}
