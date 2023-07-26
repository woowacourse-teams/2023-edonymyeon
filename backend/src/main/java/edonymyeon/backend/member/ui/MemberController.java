package edonymyeon.backend.member.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.MemberService;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.MyPageResponse;
import edonymyeon.backend.member.application.dto.PurchaseConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MyPageResponse> findMemberInfo(@AuthPrincipal MemberIdDto memberIdDto) {
        final MyPageResponse memberInfo = memberService.findMemberInfoById(memberIdDto.id());
        return ResponseEntity.ok(memberInfo);
    }

    @PostMapping("/myPosts/{postId}/purchase-confirm")
    public ResponseEntity<Void> confirmPurchase(@AuthPrincipal final MemberIdDto memberIdDto,
                                                @PathVariable final Long postId,
                                                @RequestBody final PurchaseConfirmRequest purchaseConfirmRequest) {
        memberService.confirmPurchase(memberIdDto, postId, purchaseConfirmRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/myPosts/{postId}/saving-confirm")
    public ResponseEntity<Void> confirmSaving(@AuthPrincipal final MemberIdDto memberIdDto,
                                              @PathVariable final Long postId) {
        memberService.confirmSaving(memberIdDto, postId);
        return ResponseEntity.ok().build();
    }
}
