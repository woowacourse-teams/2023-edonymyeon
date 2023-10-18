package edonymyeon.backend.setting.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.setting.application.SettingService;
import edonymyeon.backend.setting.application.dto.SettingRequest;
import edonymyeon.backend.setting.application.dto.SettingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/preference")
public class SettingController {

    private final SettingService settingService;

    @ApiVersion(value = {1, 2})
    @GetMapping("/notification")
    public ResponseEntity<SettingsResponse> findSettings(@AuthPrincipal MemberId memberId) {
        return ResponseEntity.ok(settingService.findSettingsByMemberId(memberId));
    }

    @ApiVersion(value = {1, 2})
    @PostMapping("/notification")
    public ResponseEntity<SettingsResponse> toggleSetting(@RequestBody SettingRequest settingRequest,
                                                          @AuthPrincipal MemberId memberId) {
        settingService.toggleSetting(settingRequest.preferenceType(), memberId);
        return ResponseEntity.ok(settingService.findSettingsByMemberId(memberId));
    }
}
