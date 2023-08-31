package edonymyeon.backend.setting.application;

import edonymyeon.backend.auth.application.event.JoinMemberEvent;
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
