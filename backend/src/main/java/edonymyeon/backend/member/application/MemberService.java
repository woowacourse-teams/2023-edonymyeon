package edonymyeon.backend.member.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.MyPageResponse;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final ConsumptionConfirmService consumptionConfirmService;

    public MyPageResponse findMemberInfoById(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
        return new MyPageResponse(member.getId(), member.getNickname());
    }

    public void confirmPurchase(
            final MemberIdDto memberIdDto,
            final Long postId,
            final PurchaseConfirmRequest purchaseConfirmRequest
    ) {
        consumptionConfirmService.confirmPurchase(memberIdDto, postId, purchaseConfirmRequest);
    }

    public void confirmSaving(
            final MemberIdDto memberIdDto,
            final Long postId,
            final SavingConfirmRequest savingConfirmRequest) {
        consumptionConfirmService.confirmSaving(memberIdDto, postId, savingConfirmRequest);
    }

    public void removeConfirm(final MemberIdDto memberIdDto, final Long postId) {
        consumptionConfirmService.removeConfirm(memberIdDto, postId);
    }
}
