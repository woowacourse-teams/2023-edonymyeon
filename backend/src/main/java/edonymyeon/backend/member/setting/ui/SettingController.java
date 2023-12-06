package edonymyeon.backend.member.setting.ui;

import edonymyeon.backend.member.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.profile.application.dto.MemberId;
import edonymyeon.backend.member.setting.application.SettingService;
import edonymyeon.backend.member.setting.application.dto.SettingRequest;
import edonymyeon.backend.member.setting.application.dto.SettingsResponse;
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

    @ApiVersion(from = "1.0")
    @GetMapping("/notification")
    public ResponseEntity<SettingsResponse> findSettings(@AuthPrincipal MemberId memberId) {
        return ResponseEntity.ok(settingService.findSettingsByMemberId(memberId));
    }

    @ApiVersion(from = "1.0")
    @PostMapping("/notification")
    public ResponseEntity<SettingsResponse> toggleSetting(@RequestBody SettingRequest settingRequest,
                                                          @AuthPrincipal MemberId memberId) {
        settingService.toggleSetting(settingRequest.preferenceType(), memberId);
        return ResponseEntity.ok(settingService.findSettingsByMemberId(memberId));
    }
}
