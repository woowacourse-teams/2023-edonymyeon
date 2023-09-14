package edonymyeon.backend.member.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.image.profileimage.repository.ProfileImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.application.dto.request.MemberUpdateRequest;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.application.dto.response.MemberUpdateResponse;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import edonymyeon.backend.member.domain.Device;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final DeviceRepository deviceRepository;

    private final MemberRepository memberRepository;

    private final MemberConsumptionService memberConsumptionService;

    private final ProfileImageInfoRepository profileImageInfoRepository;

    private final ImageFileUploader imageFileUploader;

    public MyPageResponse findMemberInfoById(final Long id) {
        final Member member = findMember(id);
        return new MyPageResponse(member.getId(), member.getNickname());
    }

    private Member findMember(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
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
    public MemberUpdateResponse updateMember(final MemberId memberId, final MemberUpdateRequest updateRequest) {
        final Member member = findMember(memberId.id());
        validateDuplicateNickname(member.getNickname(), updateRequest.nickname());

        if (isImageEmpty(updateRequest.profileImage())) {
            return new MemberUpdateResponse(member.getId());
        }

        final ImageInfo imageInfo = imageFileUploader.uploadFile(updateRequest.profileImage());
        final ProfileImageInfo profileImage = ProfileImageInfo.from(imageInfo);
        profileImageInfoRepository.save(profileImage);

        member.updateMember(updateRequest.nickname(), profileImage);
        return new MemberUpdateResponse(member.getId());
    }

    private boolean isImageEmpty(final MultipartFile image) {
        return Objects.isNull(image) ||
                image.isEmpty();
    }

    private void validateDuplicateNickname(final String currentNickname, final String updateNickname) {
        if (!currentNickname.equals(updateNickname) &&
                memberRepository.findByNickname(updateNickname).isPresent()) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
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
        final Member rePersistedMember = memberRepository.save(member);
        if (rePersistedMember.isActiveDevice(deviceToken)) {
            return;
        }
        rePersistedMember.activateDevice(deviceToken);
    }

    @Transactional
    public void deactivateDevice(final String deviceToken) {
        final Optional<Device> device = deviceRepository.findByDeviceToken(deviceToken);
        device.ifPresent(Device::deactivate);
    }
}
