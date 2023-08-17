package edonymyeon.backend.member.application;

import edonymyeon.backend.auth.application.event.LoginEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MemberEventListener {

    private final MemberService memberService;

    @TransactionalEventListener
    public void activateDevice(LoginEvent event) {
        memberService.activateDevice(event.member(), event.deviceToken());
    }
}