package edonymyeon.backend.member.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/profile")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MyPageResponse> findMemberInfo(@AuthPrincipal final MemberIdDto memberIdDto) {
        final MyPageResponse memberInfo = memberService.findMemberInfoById(memberIdDto.id());
        return ResponseEntity.ok(memberInfo);
    }

    @PostMapping("/my-posts/{postId}/purchase-confirm")
    public ResponseEntity<Void> confirmPurchase(@AuthPrincipal final MemberIdDto memberIdDto,
                                                @PathVariable final Long postId,
                                                @RequestBody final PurchaseConfirmRequest purchaseConfirmRequest) {
        memberService.confirmPurchase(memberIdDto, postId, purchaseConfirmRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/my-posts/{postId}/saving-confirm")
    public ResponseEntity<Void> confirmSaving(@AuthPrincipal final MemberIdDto memberIdDto,
                                              @PathVariable final Long postId,
                                              @RequestBody final SavingConfirmRequest savingConfirmRequest) {
        memberService.confirmSaving(memberIdDto, postId, savingConfirmRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/my-posts/{postId}/confirm-remove")
    public ResponseEntity<Void> removeConfirm(@AuthPrincipal final MemberIdDto memberIdDto,
                                              @PathVariable final Long postId) {
        memberService.removeConfirm(memberIdDto, postId);
        return ResponseEntity.ok().build();
    }
}
