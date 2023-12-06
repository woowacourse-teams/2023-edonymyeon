package edonymyeon.backend.member.setting.application.dto;

import edonymyeon.backend.member.setting.domain.Setting;
import java.util.List;

public record SettingsResponse(List<SettingResponse> notifications) {
    public static SettingsResponse from(final List<Setting> settings) {
        return new SettingsResponse(settings.stream()
                .map(SettingResponse::from)
                .toList());
    }
}
