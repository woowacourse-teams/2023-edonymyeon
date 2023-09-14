package edonymyeon.backend.member.application;

import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.application.dto.YearMonthDto;

public interface MemberConsumptionService {

    void confirmSaving(
            final MemberId memberId,
            final Long postId,
            final YearMonthDto yearMonth
    );

    void confirmPurchase(
            final MemberId memberId,
            final Long postId,
            final Long purchasePrice,
            final YearMonthDto yearMonth
    );

    void removeConfirm(final MemberId memberId, final Long postId);
}
