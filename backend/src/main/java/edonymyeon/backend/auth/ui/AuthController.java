package edonymyeon.backend.auth.ui;

import static edonymyeon.backend.auth.ui.SessionConst.USER;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.KakaoAuthResponseProvider;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginRequest;
import edonymyeon.backend.auth.application.dto.KakaoLoginResponse;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import edonymyeon.backend.auth.application.dto.LogoutRequest;
import edonymyeon.backend.member.application.dto.MemberId;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest,
                                      HttpSession session) {
        final MemberId member = authService.login(loginRequest);
        registerSession(session, member);
        return ResponseEntity.ok()
                .location(URI.create("/"))
                .build();
    }

    private void registerSession(final HttpSession session, final MemberId member) {
        session.setAttribute(USER.getSessionId(), member.id());
        session.setMaxInactiveInterval(USER.getValidatedTime());
    }

    @PostMapping("/auth/kakao/login")
    public ResponseEntity<Void> loginWithKakao(@RequestBody KakaoLoginRequest loginRequest,
                                               HttpSession session) {
        final KakaoLoginResponse kakaoLoginResponse = kakaoAuthResponseProvider.request(loginRequest);

        final MemberId member = authService.loginByKakao(kakaoLoginResponse, loginRequest.deviceToken());
        registerSession(session, member);
        return ResponseEntity.ok()
                .location(URI.create("/"))
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
        authService.deleteMember(memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest, HttpSession session) {
        if (Objects.nonNull(session)) {
            session.invalidate();
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
