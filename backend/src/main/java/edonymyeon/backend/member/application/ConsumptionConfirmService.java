package edonymyeon.backend.member.application;

import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.YearMonthDto;

public interface ConsumptionConfirmService {

    void confirm(
            final MemberIdDto memberIdDto,
            final Long postId,
            final Long purchasePrice,
            final YearMonthDto yearMonth
    );

    void removeConfirm(final MemberIdDto memberIdDto, final Long postId);
}
