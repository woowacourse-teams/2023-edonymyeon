package edonymyeon.backend.preference.application;

import edonymyeon.backend.auth.application.event.JoinMemberEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class PreferenceEventListener {

    private final PreferenceService preferenceService;

    @TransactionalEventListener
    public void initializePreferenceAfterJoining(JoinMemberEvent event) {
        preferenceService.initializeMemberPreference(event.member());
    }
}
