package edonymyeon.backend.member.setting.application.dto;

import edonymyeon.backend.member.setting.domain.Setting;

public record SettingResponse(String preferenceType, boolean enabled) {
    public static SettingResponse from(final Setting setting) {
        return new SettingResponse(setting.getSerialNumber(), setting.isActive());
    }
}
