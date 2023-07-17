package edonymyeon.backend.controller;

import edonymyeon.backend.service.AuthenticationService;
import edonymyeon.backend.service.request.LoginRequest;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        authenticationService.findMember(loginRequest);

        String valueToEncode = loginRequest.email() + ":" + loginRequest.password();
        final String cookie = "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).build();
    }
}
