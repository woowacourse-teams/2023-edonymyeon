package edonymyeon.backend.membber.setting.application;

import edonymyeon.backend.membber.auth.application.event.JoinMemberEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SettingEventListener {

    private final SettingService settingService;

    @EventListener
    public void initializeSettings(JoinMemberEvent event) {
        settingService.initializeSettings(event.member());
    }
}
