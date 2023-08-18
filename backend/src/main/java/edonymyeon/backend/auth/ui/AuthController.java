package edonymyeon.backend.auth.ui;

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
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    private final KakaoAuthResponseProvider kakaoAuthResponseProvider;

    private final TokenGenerator tokenGenerator;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        authService.login(loginRequest);
        final String basicToken = tokenGenerator.getBasicToken(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, basicToken)
                .build();
    }

    @PostMapping("/auth/kakao/login")
    public ResponseEntity<Void> loginWithKakao(@RequestBody KakaoLoginRequest loginRequest) {
        final KakaoLoginResponse kakaoLoginResponse = kakaoAuthResponseProvider.request(loginRequest);

        final MemberResponse memberResponse = authService.loginByKakao(kakaoLoginResponse);
        final String basicToken = tokenGenerator.getBasicToken(memberResponse.email(), memberResponse.password());
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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest, HttpServletRequest request) {
        authService.logout(request, logoutRequest.deviceToken());
        return ResponseEntity.ok().build();
    }
}
