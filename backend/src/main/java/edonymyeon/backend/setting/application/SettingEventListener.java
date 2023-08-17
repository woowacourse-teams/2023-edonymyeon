package edonymyeon.backend.setting.application;

import edonymyeon.backend.auth.application.event.JoinMemberEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class SettingEventListener {

    private final SettingService settingService;

    @TransactionalEventListener
    public void initializeSettingsAfterJoining(JoinMemberEvent event) {
        settingService.initializeSettings(event.member());
    }
}
