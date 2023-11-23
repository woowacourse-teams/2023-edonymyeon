package edonymyeon.backend.membber.member.application;

import edonymyeon.backend.membber.auth.application.event.JoinMemberEvent;
import edonymyeon.backend.membber.auth.application.event.LoginEvent;
import edonymyeon.backend.membber.auth.application.event.LogoutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class MemberEventListener {

    private final MemberService memberService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void activateDevice(LoginEvent event) {
        memberService.activateDevice(event.member(), event.deviceToken());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void activateDevice(JoinMemberEvent event) {
        memberService.deactivateOtherDevices(event.member(), event.deviceToken());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void deactivateDevice(LogoutEvent event) {
        memberService.deactivateDevice(event.deviceToken());
    }
}
