package edonymyeon.backend.auth.ui;

import edonymyeon.backend.auth.application.AuthService;
import edonymyeon.backend.auth.application.dto.DuplicateCheckResponse;
import edonymyeon.backend.auth.application.dto.JoinRequest;
import edonymyeon.backend.auth.application.dto.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Base64;
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

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        authService.findMember(loginRequest);
        final String basicAuthValue = getBasicAuthValue(loginRequest);
        final Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, basicAuthValue);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    private String getBasicAuthValue(final LoginRequest loginRequest) {
        String valueToEncode = loginRequest.email() + ":" + loginRequest.password();
        return Base64.getEncoder().encodeToString(valueToEncode.getBytes());
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
}
