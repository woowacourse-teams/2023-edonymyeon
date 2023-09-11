package edonymyeon.backend.setting.application.dto;

import edonymyeon.backend.setting.domain.Setting;

public record SettingResponse(String preferenceType, boolean enabled) {
    public static SettingResponse from(final Setting setting) {
        return new SettingResponse(setting.getSerialNumber(), setting.isActive());
    }
}
