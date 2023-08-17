package edonymyeon.backend.member.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.profileimage.repository.ProfileImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberConsumptionService memberConsumptionService;

    private final ProfileImageInfoRepository profileImageInfoRepository;

    private final ImageFileUploader imageFileUploader;

    public MyPageResponse findMemberInfoById(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
        return new MyPageResponse(member.getId(), member.getNickname());
    }

    @Transactional
    public void deleteMember(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));

        if (Objects.nonNull(member.getProfileImageInfo())) {
            profileImageInfoRepository.delete(member.getProfileImageInfo());
            imageFileUploader.removeFile(member.getProfileImageInfo());
        }

        member.withdraw();
        memberRepository.save(member);
    }

    @Transactional
    public void confirmPurchase(
            final MemberId memberId,
            final Long postId,
            final PurchaseConfirmRequest purchaseConfirmRequest
    ) {
        final YearMonthDto yearMonthDto = new YearMonthDto(purchaseConfirmRequest.year(),
                purchaseConfirmRequest.month());
        memberConsumptionService.confirmPurchase(memberId, postId, purchaseConfirmRequest.purchasePrice(),
                yearMonthDto);
    }

    @Transactional
    public void confirmSaving(
            final MemberId memberId,
            final Long postId,
            final SavingConfirmRequest savingConfirmRequest) {
        final YearMonthDto yearMonthDto = new YearMonthDto(savingConfirmRequest.year(),
                savingConfirmRequest.month());
        memberConsumptionService.confirmSaving(memberId, postId, yearMonthDto);
    }

    @Transactional
    public void removeConfirm(final MemberId memberId, final Long postId) {
        memberConsumptionService.removeConfirm(memberId, postId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void activateDevice(final Member member, final String deviceToken) {
        if (member.isActiveDevice(deviceToken)) {
            return;
        }
        member.activateDevice(deviceToken);
    }
}
