package edonymyeon.backend.member.application;

import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;

public interface ConsumptionConfirmService {

    void confirmPurchase(
            final MemberIdDto memberIdDto,
            final Long postId,
            final PurchaseConfirmRequest purchaseConfirmRequest
    );

    void confirmSaving(
            final MemberIdDto memberIdDto,
            final Long postId,
            final SavingConfirmRequest savingConfirmRequest
    );

    void removeConfirm(final MemberIdDto memberIdDto, final Long postId);
}
