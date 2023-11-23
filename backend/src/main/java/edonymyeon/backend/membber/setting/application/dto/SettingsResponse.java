package edonymyeon.backend.membber.setting.application.dto;

import edonymyeon.backend.membber.setting.domain.Setting;
import java.util.List;

public record SettingsResponse(List<SettingResponse> notifications) {
    public static SettingsResponse from(final List<Setting> settings) {
        return new SettingsResponse(settings.stream()
                .map(SettingResponse::from)
                .toList());
    }
}
