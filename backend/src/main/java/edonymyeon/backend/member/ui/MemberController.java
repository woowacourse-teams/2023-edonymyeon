package edonymyeon.backend.member.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.application.dto.request.MemberUpdateRequest;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.application.dto.response.DuplicateCheckResponse;
import edonymyeon.backend.member.application.dto.response.MemberUpdateResponse;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @ApiVersion(value = {1})
    @GetMapping("/profile")
    public ResponseEntity<MyPageResponse> findMemberInfo(@AuthPrincipal MemberId memberId) {
        final MyPageResponse memberInfo = memberService.findMemberInfoById(memberId.id());
        return ResponseEntity.ok(memberInfo);
    }

    @ApiVersion(value = {1})
    @PutMapping("/profile")
    public ResponseEntity<MemberUpdateResponse> updateMember(@AuthPrincipal MemberId memberId,
                                                             @ModelAttribute MemberUpdateRequest updateRequest) {
        final MemberUpdateResponse memberResponse = memberService.updateMember(memberId, updateRequest);
        return ResponseEntity.ok(memberResponse);
    }

    @ApiVersion(value = {1})
    @GetMapping("/members/check")
    public ResponseEntity<DuplicateCheckResponse> validateDuplicate(@RequestParam String target,
                                                                    @RequestParam String value) {
        final DuplicateCheckResponse duplicateCheckResponse = memberService.checkDuplicate(target, value);
        return ResponseEntity.ok().body(duplicateCheckResponse);
    }

    @ApiVersion(value = {1})
    @PostMapping("/profile/my-posts/{postId}/purchase-confirm")
    public ResponseEntity<Void> confirmPurchase(@AuthPrincipal final MemberId memberId,
                                                @PathVariable final Long postId,
                                                @RequestBody final PurchaseConfirmRequest purchaseConfirmRequest) {
        memberService.confirmPurchase(memberId, postId, purchaseConfirmRequest);
        return ResponseEntity.ok().build();
    }

    @ApiVersion(value = {1})
    @PostMapping("/profile/my-posts/{postId}/saving-confirm")
    public ResponseEntity<Void> confirmSaving(@AuthPrincipal final MemberId memberId,
                                              @PathVariable final Long postId,
                                              @RequestBody final SavingConfirmRequest savingConfirmRequest) {
        memberService.confirmSaving(memberId, postId, savingConfirmRequest);
        return ResponseEntity.ok().build();
    }

    @ApiVersion(value = {1})
    @DeleteMapping("/profile/my-posts/{postId}/confirm-remove")
    public ResponseEntity<Void> removeConfirm(@AuthPrincipal final MemberId memberId,
                                              @PathVariable final Long postId) {
        memberService.removeConfirm(memberId, postId);
        return ResponseEntity.ok().build();
    }
}
